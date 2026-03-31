#!/usr/bin/env python3
import argparse
import calendar
import datetime as dt
import random
import re
import uuid
from collections import defaultdict
from dataclasses import dataclass
from decimal import Decimal, ROUND_HALF_UP
from pathlib import Path
from typing import Dict, List, Optional, Tuple

TWO_DP = Decimal("0.01")
ZERO = Decimal("0.00")


@dataclass
class TableDump:
    columns: List[str]
    rows: List[Dict[str, Optional[str]]]


def q2(value: Decimal) -> Decimal:
    return value.quantize(TWO_DP, rounding=ROUND_HALF_UP)


def nvl(value: Optional[Decimal]) -> Decimal:
    if value is None:
        return ZERO
    return q2(value)


def to_decimal(raw: Optional[str]) -> Decimal:
    if raw is None or raw == "":
        return ZERO
    return q2(Decimal(str(raw)))


def parse_date(raw: Optional[str]) -> Optional[dt.date]:
    if raw in (None, ""):
        return None
    return dt.date.fromisoformat(raw)


def parse_time(raw: Optional[str]) -> Optional[dt.time]:
    if raw in (None, ""):
        return None
    text = raw.split(".")[0]
    return dt.time.fromisoformat(text)


def parse_bool(raw: Optional[str]) -> bool:
    return str(raw).lower() in {"t", "true", "1"}


def sql_literal(value):
    if value is None:
        return "NULL"
    if isinstance(value, bool):
        return "true" if value else "false"
    if isinstance(value, Decimal):
        return str(q2(value))
    if isinstance(value, (int, float)):
        return str(value)
    if isinstance(value, dt.datetime):
        return "'" + value.isoformat(sep=" ") + "'"
    if isinstance(value, dt.date):
        return "'" + value.isoformat() + "'"
    if isinstance(value, dt.time):
        return "'" + value.isoformat() + "'"
    text = str(value).replace("'", "''")
    return f"'{text}'"


def random_uuid(rng: random.Random) -> str:
    return str(uuid.UUID(int=rng.getrandbits(128), version=4))


def parse_table_columns(sql_text: str) -> Dict[str, Dict[str, str]]:
    table_columns: Dict[str, Dict[str, str]] = {}
    pattern = re.compile(r"CREATE TABLE public\.(\w+) \((.*?)\);", re.S)
    for table_name, body in pattern.findall(sql_text):
        col_map: Dict[str, str] = {}
        for line in body.splitlines():
            stripped = line.strip().rstrip(",")
            if not stripped or stripped.upper().startswith("CONSTRAINT"):
                continue
            if " " not in stripped:
                continue
            first, rest = stripped.split(" ", 1)
            if first.lower() in {"primary", "unique", "foreign", "check"}:
                continue
            col_map[first.strip('"')] = rest.strip()
        table_columns[table_name] = col_map
    return table_columns


def parse_dump(sql_text: str) -> Dict[str, TableDump]:
    tables: Dict[str, TableDump] = {}
    copy_pattern = re.compile(r"COPY public\.(\w+) \((.*?)\) FROM stdin;\r?\n", re.S)

    index = 0
    while True:
        match = copy_pattern.search(sql_text, index)
        if not match:
            break

        table = match.group(1)
        columns = [c.strip() for c in match.group(2).split(",")]

        data_start = match.end()
        marker_match = re.search(r"(?:^|\r?\n)\\\.\r?\n", sql_text[data_start:])
        if marker_match is None:
            break
        data_end = data_start + marker_match.start()

        rows: List[Dict[str, Optional[str]]] = []
        data_blob = sql_text[data_start:data_end]
        for line in data_blob.splitlines():
            if not line.strip():
                continue
            parts = line.split("\t")
            row: Dict[str, Optional[str]] = {}
            for idx, col in enumerate(columns):
                raw = parts[idx] if idx < len(parts) else None
                row[col] = None if raw == r"\N" else raw
            rows.append(row)

        tables[table] = TableDump(columns=columns, rows=rows)
        index = data_start + marker_match.end()

    return tables


def get_setting_value(
    settings_rows: List[Dict[str, Optional[str]]],
    key_name: str,
    effective_date: dt.date,
) -> Decimal:
    candidates = [
        row
        for row in settings_rows
        if row.get("key_name") == key_name
        and parse_date(row.get("effective_date")) is not None
        and parse_date(row.get("effective_date")) <= effective_date
    ]
    if not candidates:
        return ZERO
    candidates.sort(key=lambda x: parse_date(x.get("effective_date")))
    return to_decimal(candidates[-1].get("value"))


def normalize_rate(rate: Decimal) -> Decimal:
    rate = nvl(rate)
    if rate >= Decimal("1"):
        return (rate / Decimal("100")).quantize(Decimal("0.000001"), rounding=ROUND_HALF_UP)
    return rate.quantize(Decimal("0.000001"), rounding=ROUND_HALF_UP)


def get_insurance_rate(
    insurance_rows: List[Dict[str, Optional[str]]],
    insurance_type: str,
    effective_date: dt.date,
) -> Decimal:
    candidates = [
        row
        for row in insurance_rows
        if row.get("type") == insurance_type
        and parse_date(row.get("effective_date")) is not None
        and parse_date(row.get("effective_date")) <= effective_date
    ]
    if not candidates:
        return ZERO
    candidates.sort(key=lambda x: parse_date(x.get("effective_date")))
    return normalize_rate(to_decimal(candidates[-1].get("employee_rate")))


def find_tax_bracket(
    tax_rows: List[Dict[str, Optional[str]]],
    income: Decimal,
    effective_date: dt.date,
) -> Optional[Dict[str, Optional[str]]]:
    active = [
        row
        for row in tax_rows
        if parse_bool(row.get("is_active"))
        and parse_date(row.get("effective_date")) is not None
        and parse_date(row.get("effective_date")) <= effective_date
    ]
    if not active:
        active = [row for row in tax_rows if parse_bool(row.get("is_active"))]
    if not active:
        return None

    active.sort(key=lambda x: to_decimal(x.get("min_income")))

    for bracket in active:
        min_income = to_decimal(bracket.get("min_income"))
        max_raw = bracket.get("max_income")
        if max_raw is None:
            max_ok = True
        else:
            max_ok = to_decimal(max_raw) >= income
        if min_income <= income and max_ok:
            return bracket
    return active[-1]


def calculate_tax(
    tax_rows: List[Dict[str, Optional[str]]],
    assessable_income: Decimal,
    effective_date: dt.date,
) -> Tuple[Decimal, Decimal]:
    bracket = find_tax_bracket(tax_rows, assessable_income, effective_date)
    if not bracket:
        return ZERO, ZERO

    rate_raw = bracket.get("tax_rate")
    rate_pct = Decimal(str(rate_raw)) if rate_raw not in (None, "") else Decimal("0")
    rate = (rate_pct / Decimal("100")).quantize(Decimal("0.000001"), rounding=ROUND_HALF_UP)

    tax_before_subtract = q2(assessable_income * rate)
    subtract_amount = to_decimal(bracket.get("subtract_amount"))
    tax = q2(max(ZERO, tax_before_subtract - subtract_amount))
    return tax, subtract_amount


def divide(numerator: Decimal, denominator: Decimal) -> Decimal:
    if denominator == ZERO:
        return ZERO
    return q2(numerator / denominator)


def compute_expected_shift_hours(shift_row: Dict[str, Optional[str]]) -> Decimal:
    start = parse_time(shift_row.get("start_time"))
    end = parse_time(shift_row.get("end_time"))
    if not start or not end:
        return Decimal("8.00")

    start_min = start.hour * 60 + start.minute
    end_min = end.hour * 60 + end.minute
    if end_min < start_min:
        end_min += 24 * 60
    hours = Decimal(end_min - start_min) / Decimal(60)
    return q2(hours)


def build_insert_sql(
    table_name: str,
    rows: List[Dict[str, object]],
    schema_columns: Dict[str, str],
    preferred_order: List[str],
) -> str:
    if not rows:
        return ""

    keep_cols = [c for c in preferred_order if c in schema_columns]
    values_sql = []
    for row in rows:
        vals = [sql_literal(row.get(col)) for col in keep_cols]
        values_sql.append("(" + ", ".join(vals) + ")")

    return (
        f"INSERT INTO public.{table_name} (" + ", ".join(keep_cols) + ") VALUES\\n"
        + ",\\n".join(values_sql)
        + ";\\n"
    )


def main() -> None:
    parser = argparse.ArgumentParser(description="Generate controlled, large payroll test data based on BE formulas.")
    parser.add_argument("--input", default="db.sql", help="Path to source SQL dump.")
    parser.add_argument("--output", default="generated_payroll_data.sql", help="Output SQL file path.")
    parser.add_argument("--seed", type=int, default=42, help="Random seed for reproducible data.")
    parser.add_argument("--periods", type=int, default=6, help="Number of payroll periods to generate.")
    parser.add_argument("--employees", type=int, default=200, help="Target number of employees after generation.")
    parser.add_argument("--max-adjustments", type=int, default=2, help="Max salary adjustments per employee per period.")
    parser.add_argument("--validate-existing", action="store_true", help="Validate existing payslips in input dump.")
    args = parser.parse_args()

    rng = random.Random(args.seed)

    sql_text = Path(args.input).read_text(encoding="utf-8")
    schema = parse_table_columns(sql_text)
    tables = parse_dump(sql_text)

    required = [
        "employees",
        "positions",
        "departments",
        "contracts",
        "allowance_types",
        "contract_allowances",
        "work_shifts",
        "sys_settings",
        "tax_brackets",
        "insurance_configs",
        "payroll_periods",
        "daily_attendances",
        "attendance_summaries",
        "salary_adjustments",
        "dependents",
        "payslips",
    ]
    for table in required:
        if table not in tables:
            tables[table] = TableDump(columns=[], rows=[])

    employees = list(tables["employees"].rows)
    positions = list(tables["positions"].rows)
    allowance_types = list(tables["allowance_types"].rows)
    work_shifts = list(tables["work_shifts"].rows)
    contracts = list(tables["contracts"].rows)
    contract_allowances = list(tables["contract_allowances"].rows)
    settings = list(tables["sys_settings"].rows)
    tax_brackets = list(tables["tax_brackets"].rows)
    insurance = list(tables["insurance_configs"].rows)
    dependents_all = list(tables["dependents"].rows)

    if not employees or not positions or not work_shifts:
        raise SystemExit("Missing core seed data (employees/positions/work_shifts).")

    position_by_id = {p["id"]: p for p in positions}
    shift_by_id = {s["id"]: s for s in work_shifts}
    allowance_by_id = {a["id"]: a for a in allowance_types}

    if args.validate_existing:
        existing_payslips = [r for r in tables["payslips"].rows if not parse_bool(r.get("is_deleted"))]
        missing_link = 0
        negative_values = 0
        equation_mismatch = 0
        checked_equation = 0

        for row in existing_payslips:
            if not row.get("employee_id") or not row.get("payroll_period_id") or not row.get("contract_id"):
                missing_link += 1
                continue

            net_salary = to_decimal(row.get("net_salary"))
            tax_amount = to_decimal(row.get("tax_amount"))
            taxable_income = to_decimal(row.get("taxable_income"))
            salary_by_workdays = to_decimal(row.get("salary_by_workdays"))
            if min(net_salary, tax_amount, taxable_income, salary_by_workdays) < ZERO:
                negative_values += 1

            # Basic balance check aligned with BE fields if they exist in table.
            if "assessable_income" in row and "total_deductions" in row:
                assessable = to_decimal(row.get("assessable_income"))
                total_ded = to_decimal(row.get("total_deductions"))
                checked_equation += 1
                expected_assessable = q2(max(ZERO, taxable_income - total_ded))
                if abs(assessable - expected_assessable) > Decimal("0.01"):
                    equation_mismatch += 1

        print("Existing payslip validation:")
        print(f"  Total payslips checked: {len(existing_payslips)}")
        print(f"  Missing employee/period/contract links: {missing_link}")
        print(f"  Negative money fields: {negative_values}")
        print(f"  Assessable income equation mismatches: {equation_mismatch}/{checked_equation}")

    new_employees: List[Dict[str, object]] = []
    new_contracts: List[Dict[str, object]] = []
    new_contract_allowances: List[Dict[str, object]] = []
    new_dependents: List[Dict[str, object]] = []
    new_periods: List[Dict[str, object]] = []
    new_attendances: List[Dict[str, object]] = []
    new_summaries: List[Dict[str, object]] = []
    new_adjustments: List[Dict[str, object]] = []
    new_payslips: List[Dict[str, object]] = []

    # Grow employees with consistent FK to position/department and active contract.
    employee_count_needed = max(args.employees - len(employees), 0)
    for i in range(employee_count_needed):
        pos = rng.choice(positions)
        emp_id = random_uuid(rng)
        emp_code = f"GEN{(i + 1):05d}"
        created_at = dt.datetime.now().replace(microsecond=0)

        emp_row = {
            "id": emp_id,
            "created_at": created_at,
            "created_by": "data_gen",
            "is_deleted": False,
            "updated_at": created_at,
            "updated_by": "data_gen",
            "version": 0,
            "bank_account": f"{rng.randint(1000000000, 9999999999)}",
            "bank_name": rng.choice(["Vietcombank", "Techcombank", "BIDV", "MB Bank", "ACB"]),
            "email": f"{emp_code.lower()}@example.com",
            "employee_code": emp_code,
            "full_name": f"Generated Employee {i + 1}",
            "phone": f"0{rng.randint(100000000, 999999999)}",
            "status": "ACTIVE",
            "tax_code": f"{rng.randint(100000000, 999999999)}",
            "department_id": pos.get("department_id"),
            "position_id": pos.get("id"),
        }
        new_employees.append(emp_row)
        employees.append({k: str(v) if v is not None else None for k, v in emp_row.items()})

        start_date = dt.date(2024, 1, 1)
        salary = q2(Decimal(rng.randint(8_000_000, 35_000_000)))
        contract_id = random_uuid(rng)
        contract_row = {
            "id": contract_id,
            "created_at": created_at,
            "created_by": "data_gen",
            "is_deleted": False,
            "updated_at": created_at,
            "updated_by": "data_gen",
            "version": 0,
            "actual_salary": salary,
            "base_salary": salary,
            "contract_number": f"HD-GEN-{i + 1:05d}",
            "end_date": None,
            "is_net": False,
            "start_date": start_date,
            "status": "ACTIVE",
            "employee_id": emp_id,
            "position_id": pos.get("id"),
        }
        new_contracts.append(contract_row)
        contracts.append({k: str(v) if isinstance(v, Decimal) else (v.isoformat() if isinstance(v, dt.date) else (str(v) if v is not None else None)) for k, v in contract_row.items()})

        for _ in range(rng.randint(1, 3)):
            at = rng.choice(allowance_types)
            ca_row = {
                "id": random_uuid(rng),
                "created_at": created_at,
                "created_by": "data_gen",
                "is_deleted": False,
                "updated_at": created_at,
                "updated_by": "data_gen",
                "version": 0,
                "amount": q2(Decimal(rng.randint(400_000, 5_000_000))),
                "allowance_type_id": at.get("id"),
                "contract_id": contract_id,
            }
            new_contract_allowances.append(ca_row)
            contract_allowances.append({k: str(v) if isinstance(v, Decimal) else (str(v) if v is not None else None) for k, v in ca_row.items()})

        # Dependents for deduction testing.
        dep_count = rng.randint(0, 3)
        for d_idx in range(dep_count):
            dep_row = {
                "id": random_uuid(rng),
                "created_at": created_at,
                "created_by": "data_gen",
                "is_deleted": False,
                "updated_at": created_at,
                "updated_by": "data_gen",
                "version": 0,
                "birth_date": dt.date(rng.randint(1980, 2022), rng.randint(1, 12), rng.randint(1, 28)),
                "full_name": f"Dependent {i + 1}-{d_idx + 1}",
                "is_qualified": True,
                "relationship": rng.choice(["SPOUSE", "CHILD", "PARENT"]),
                "employee_id": emp_id,
            }
            new_dependents.append(dep_row)
            dependents_all.append({k: (v.isoformat() if isinstance(v, dt.date) else str(v).lower() if isinstance(v, bool) else (str(v) if v is not None else None)) for k, v in dep_row.items()})

    # Build periods from month after latest existing period end_date.
    existing_period_end_dates = [parse_date(r.get("end_date")) for r in tables["payroll_periods"].rows if parse_date(r.get("end_date"))]
    if existing_period_end_dates:
        first_date = max(existing_period_end_dates) + dt.timedelta(days=1)
        first_date = first_date.replace(day=1)
    else:
        first_date = dt.date.today().replace(day=1)

    period_rows_for_generation = []
    for p_idx in range(args.periods):
        y = first_date.year + ((first_date.month - 1 + p_idx) // 12)
        m = ((first_date.month - 1 + p_idx) % 12) + 1
        last_day = calendar.monthrange(y, m)[1]
        start = dt.date(y, m, 1)
        end = dt.date(y, m, last_day)
        weekdays = sum(1 for d in range(1, last_day + 1) if dt.date(y, m, d).weekday() < 5)

        period_row = {
            "id": random_uuid(rng),
            "created_at": dt.datetime.now().replace(microsecond=0),
            "created_by": "data_gen",
            "is_deleted": False,
            "updated_at": dt.datetime.now().replace(microsecond=0),
            "updated_by": "data_gen",
            "version": 0,
            "end_date": end,
            "name": f"Thang {m:02d}/{y}",
            "standard_working_days": float(weekdays),
            "start_date": start,
            "status": "APPROVED",
        }
        new_periods.append(period_row)
        period_rows_for_generation.append(period_row)

    # Index contract and allowance for fast lookup.
    contracts_by_employee: Dict[str, List[Dict[str, Optional[str]]]] = defaultdict(list)
    for c in contracts:
        contracts_by_employee[str(c.get("employee_id"))].append(c)

    allowances_by_contract: Dict[str, List[Dict[str, Optional[str]]]] = defaultdict(list)
    for ca in contract_allowances:
        allowances_by_contract[str(ca.get("contract_id"))].append(ca)

    dependents_by_employee: Dict[str, int] = defaultdict(int)
    for dep in dependents_all:
        if parse_bool(dep.get("is_deleted")):
            continue
        if not parse_bool(dep.get("is_qualified")):
            continue
        emp_id = dep.get("employee_id")
        if emp_id:
            dependents_by_employee[str(emp_id)] += 1

    # Generate daily attendance, summary, salary adjustment, then payslip.
    all_employees_for_generation = employees[: args.employees]

    for period in period_rows_for_generation:
        period_id = str(period["id"])
        start_date = period["start_date"]
        end_date = period["end_date"]
        standard_days = q2(Decimal(str(period["standard_working_days"])))

        for emp in all_employees_for_generation:
            emp_id = str(emp.get("id"))
            shift = rng.choice(work_shifts)
            shift_id = shift.get("id")
            expected_hours = compute_expected_shift_hours(shift)

            actual_count = 0
            paid_leave_count = 0
            unpaid_leave_count = 0
            overtime = Decimal("0.00")

            current = start_date
            while current <= end_date:
                if current.weekday() >= 5:
                    current += dt.timedelta(days=1)
                    continue

                roll = rng.random()
                if roll < 0.74:
                    status = "OK"
                elif roll < 0.84:
                    status = "LATE"
                elif roll < 0.90:
                    status = "EARLY"
                elif roll < 0.95:
                    status = "LEAVE"
                else:
                    status = "ABSENT"

                late_minutes = 0
                early_minutes = 0
                working_hours = Decimal("0.00")
                check_in = None
                check_out = None

                if status in {"OK", "LATE", "EARLY"}:
                    base_hours = expected_hours + Decimal(str(rng.uniform(-0.8, 1.5)))
                    working_hours = q2(max(Decimal("4.00"), base_hours))
                    if status == "LATE":
                        late_minutes = rng.randint(5, 45)
                    if status == "EARLY":
                        early_minutes = rng.randint(5, 45)

                    check_in = dt.datetime.combine(current, dt.time(8, 0)) + dt.timedelta(minutes=late_minutes)
                    check_out = check_in + dt.timedelta(hours=float(working_hours)) - dt.timedelta(minutes=early_minutes)

                if status in {"OK", "LATE"}:
                    actual_count += 1
                if status == "LEAVE":
                    paid_leave_count += 1
                if status == "ABSENT":
                    unpaid_leave_count += 1
                if status == "OK" and working_hours > expected_hours:
                    overtime += q2(working_hours - expected_hours)

                da_row = {
                    "id": random_uuid(rng),
                    "created_at": period["created_at"],
                    "created_by": "data_gen",
                    "is_deleted": False,
                    "updated_at": period["updated_at"],
                    "updated_by": "data_gen",
                    "version": 0,
                    "check_in_time": check_in,
                    "check_out_time": check_out,
                    "date": current,
                    "early_minutes": early_minutes,
                    "late_minutes": late_minutes,
                    "status": status,
                    "working_hours": float(working_hours),
                    "employee_id": emp_id,
                    "shift_id": shift_id,
                }
                new_attendances.append(da_row)
                current += dt.timedelta(days=1)

            summary_row = {
                "id": random_uuid(rng),
                "created_at": period["created_at"],
                "created_by": "data_gen",
                "is_deleted": False,
                "updated_at": period["updated_at"],
                "updated_by": "data_gen",
                "version": 0,
                "actual_working_days": float(actual_count),
                "overtime_hours": float(q2(overtime)),
                "paid_leave_days": float(paid_leave_count),
                "reason": None,
                "status": "APPROVED",
                "unpaid_leave_days": float(unpaid_leave_count),
                "employee_id": emp_id,
                "payroll_period_id": period_id,
            }
            new_summaries.append(summary_row)

            # Salary adjustments with same logic shape as BE.
            adjustment_count = rng.randint(0, args.max_adjustments)
            for _ in range(adjustment_count):
                adj_row = {
                    "id": random_uuid(rng),
                    "created_at": period["created_at"],
                    "created_by": "data_gen",
                    "is_deleted": False,
                    "updated_at": period["updated_at"],
                    "updated_by": "data_gen",
                    "version": 0,
                    "amount": q2(Decimal(rng.randint(200_000, 3_000_000))),
                    "is_net_only": False,
                    "is_taxable": rng.choice([True, False]),
                    "reason": rng.choice(["Bonus KPI", "Fine", "Advance"]),
                    "status": "APPROVED",
                    "type": rng.choice(["BONUS", "FINE", "ADVANCE"]),
                    "employee_id": emp_id,
                    "payroll_period_id": period_id,
                }
                new_adjustments.append(adj_row)

            # Compute payslip based on BE formula (including adjustment behavior in service).
            eligible_contracts = []
            for c in contracts_by_employee.get(emp_id, []):
                if parse_bool(c.get("is_deleted")):
                    continue
                if c.get("status") != "ACTIVE":
                    continue
                c_start = parse_date(c.get("start_date"))
                c_end = parse_date(c.get("end_date"))
                if c_start and c_start <= end_date and (c_end is None or c_end >= end_date):
                    eligible_contracts.append(c)
            eligible_contracts.sort(key=lambda c: parse_date(c.get("start_date")) or dt.date.min, reverse=True)
            if not eligible_contracts:
                continue
            contract = eligible_contracts[0]
            contract_id = str(contract.get("id"))

            gross_salary = to_decimal(contract.get("actual_salary"))
            actual_days = q2(Decimal(str(actual_count)))
            salary_by_workdays = divide(gross_salary * actual_days, standard_days)

            tax_in_allow = ZERO
            tax_non_in_allow = ZERO
            non_tax_in_allow = ZERO
            non_tax_non_in_allow = ZERO

            for ca in allowances_by_contract.get(contract_id, []):
                amount = to_decimal(ca.get("amount"))
                at = allowance_by_id.get(ca.get("allowance_type_id"))
                if not at:
                    continue
                is_taxable = parse_bool(at.get("is_taxable"))
                is_insurance = parse_bool(at.get("is_insurance_based"))
                if is_taxable and is_insurance:
                    tax_in_allow += amount
                elif is_taxable and not is_insurance:
                    tax_non_in_allow += amount
                elif not is_taxable and is_insurance:
                    non_tax_in_allow += amount
                else:
                    non_tax_non_in_allow += amount

            # Mirrors current BE: taxable/non-taxable adjustment sums currently use same type sum.
            em_adjustments = [
                a
                for a in new_adjustments
                if a["employee_id"] == emp_id and a["payroll_period_id"] == period_id and a["status"] == "APPROVED"
            ]

            def sum_by_type(adjustments, adj_type):
                total = ZERO
                for a in adjustments:
                    if a.get("type") == adj_type:
                        total += to_decimal(str(a.get("amount")))
                return q2(total)

            bonus_tax = sum_by_type(em_adjustments, "BONUS")
            bonus_non_tax = sum_by_type(em_adjustments, "BONUS")
            fine_tax = sum_by_type(em_adjustments, "FINE")
            fine_non_tax = sum_by_type(em_adjustments, "FINE")
            advance_tax = sum_by_type(em_adjustments, "ADVANCE")
            advance_non_tax = sum_by_type(em_adjustments, "ADVANCE")

            taxable_adjustment = q2(bonus_tax - fine_tax - advance_tax)
            non_taxable_adjustment = q2(bonus_non_tax - fine_non_tax - advance_non_tax)

            taxable_income = q2(max(ZERO, salary_by_workdays + tax_in_allow + tax_non_in_allow + taxable_adjustment))

            bhxh_base = q2(to_decimal(contract.get("base_salary")) + tax_in_allow + non_tax_in_allow)
            bhxh_rate = get_insurance_rate(insurance, "BHXH", end_date)
            bhyt_rate = get_insurance_rate(insurance, "BHYT", end_date)
            bhtn_rate = get_insurance_rate(insurance, "BHTN", end_date)

            bhxh_amount = q2(bhxh_base * bhxh_rate)
            bhyt_amount = q2(bhxh_base * bhyt_rate)
            bhtn_amount = q2(bhxh_base * bhtn_rate)
            total_insurance = q2(bhxh_amount + bhyt_amount + bhtn_amount)

            personal_deduction = get_setting_value(settings, "GIAM_TRU_CA_NHAN", end_date)
            if personal_deduction <= ZERO:
                personal_deduction = Decimal("11000000.00")

            dependent_unit = get_setting_value(settings, "PHU_CAP_NGUOI_PHU_THUOC", end_date)
            dependent_count = dependents_by_employee.get(emp_id, 0)
            deduction_dependents = q2(dependent_unit * Decimal(dependent_count))

            total_deductions = q2(personal_deduction + deduction_dependents + total_insurance)
            assessable_income = q2(max(ZERO, taxable_income - total_deductions))
            tax_amount, tax_fixed_deduction = calculate_tax(tax_brackets, assessable_income, end_date)

            net_salary = q2(
                taxable_income
                + non_tax_in_allow
                + non_tax_non_in_allow
                + non_taxable_adjustment
                - tax_amount
            )

            status_col = schema.get("payslips", {}).get("status", "")
            if "smallint" in status_col.lower():
                status_value = 2
            else:
                status_value = "PENDING"

            payslip_row = {
                "id": random_uuid(rng),
                "created_at": period["created_at"],
                "created_by": "data_gen",
                "is_deleted": False,
                "updated_at": period["updated_at"],
                "updated_by": "data_gen",
                "version": 0,
                "assessable_income": assessable_income,
                "bhtn_amount": bhtn_amount,
                "bhxh_amount": bhxh_amount,
                "bhxh_salary_base": bhxh_base,
                "bhyt_amount": bhyt_amount,
                "deduction_dependents": deduction_dependents,
                "deduction_personal": personal_deduction,
                "gross_salary": gross_salary,
                "net_salary": net_salary,
                "reason": None,
                "salary_by_workdays": salary_by_workdays,
                "social_insurance_benefit_amount": None,
                "status": status_value,
                "tax_amount": tax_amount,
                "taxable_income": taxable_income,
                "total_allowance": q2(tax_in_allow + tax_non_in_allow + non_tax_in_allow + non_tax_non_in_allow),
                "total_bonus": taxable_adjustment,
                "contract_id": contract_id,
                "employee_id": emp_id,
                "payroll_period_id": period_id,
                # New schema columns (kept if table has them)
                "workdays": actual_days,
                "total_workdays": standard_days,
                "taxable_and_insurable_allowance": tax_in_allow,
                "taxable_and_non_insurable_allowance": tax_non_in_allow,
                "non_taxable_and_insurable_allowance": non_tax_in_allow,
                "non_taxable_and_non_insurable_allowance": non_tax_non_in_allow,
                "total_bonus_non_tax": non_taxable_adjustment,
                "tax_fixed_deduction": tax_fixed_deduction,
                "total_deductions": total_deductions,
                "tax_rate": None,
            }
            new_payslips.append(payslip_row)

    inserts = []
    inserts.append("-- Generated payroll data\n")
    inserts.append(f"-- Seed: {args.seed} | periods: {args.periods} | employees target: {args.employees}\n\n")

    inserts.append(build_insert_sql(
        "employees",
        new_employees,
        schema.get("employees", {}),
        [
            "id", "created_at", "created_by", "is_deleted", "updated_at", "updated_by", "version",
            "bank_account", "bank_name", "email", "employee_code", "full_name", "phone", "status", "tax_code",
            "department_id", "position_id",
        ],
    ))
    inserts.append(build_insert_sql(
        "contracts",
        new_contracts,
        schema.get("contracts", {}),
        [
            "id", "created_at", "created_by", "is_deleted", "updated_at", "updated_by", "version",
            "actual_salary", "base_salary", "contract_number", "end_date", "is_net", "start_date", "status",
            "employee_id", "position_id",
        ],
    ))
    inserts.append(build_insert_sql(
        "contract_allowances",
        new_contract_allowances,
        schema.get("contract_allowances", {}),
        [
            "id", "created_at", "created_by", "is_deleted", "updated_at", "updated_by", "version",
            "amount", "allowance_type_id", "contract_id",
        ],
    ))
    inserts.append(build_insert_sql(
        "dependents",
        new_dependents,
        schema.get("dependents", {}),
        [
            "id", "created_at", "created_by", "is_deleted", "updated_at", "updated_by", "version",
            "birth_date", "full_name", "is_qualified", "relationship", "employee_id",
        ],
    ))
    inserts.append(build_insert_sql(
        "payroll_periods",
        new_periods,
        schema.get("payroll_periods", {}),
        [
            "id", "created_at", "created_by", "is_deleted", "updated_at", "updated_by", "version",
            "end_date", "name", "standard_working_days", "start_date", "status",
        ],
    ))
    inserts.append(build_insert_sql(
        "daily_attendances",
        new_attendances,
        schema.get("daily_attendances", {}),
        [
            "id", "created_at", "created_by", "is_deleted", "updated_at", "updated_by", "version",
            "check_in_time", "check_out_time", "date", "early_minutes", "late_minutes", "status", "working_hours",
            "employee_id", "shift_id",
        ],
    ))
    inserts.append(build_insert_sql(
        "attendance_summaries",
        new_summaries,
        schema.get("attendance_summaries", {}),
        [
            "id", "created_at", "created_by", "is_deleted", "updated_at", "updated_by", "version",
            "actual_working_days", "overtime_hours", "paid_leave_days", "reason", "status", "unpaid_leave_days",
            "employee_id", "payroll_period_id",
        ],
    ))
    inserts.append(build_insert_sql(
        "salary_adjustments",
        new_adjustments,
        schema.get("salary_adjustments", {}),
        [
            "id", "created_at", "created_by", "is_deleted", "updated_at", "updated_by", "version",
            "amount", "is_net_only", "is_taxable", "reason", "status", "type",
            "employee_id", "payroll_period_id",
        ],
    ))
    inserts.append(build_insert_sql(
        "payslips",
        new_payslips,
        schema.get("payslips", {}),
        [
            "id", "created_at", "created_by", "is_deleted", "updated_at", "updated_by", "version",
            "assessable_income", "bhtn_amount", "bhxh_amount", "bhxh_salary_base", "bhyt_amount",
            "deduction_dependents", "deduction_personal", "gross_salary", "net_salary", "reason", "salary_by_workdays",
            "social_insurance_benefit_amount", "status", "tax_amount", "taxable_income", "total_allowance", "total_bonus",
            "contract_id", "employee_id", "payroll_period_id",
            "workdays", "total_workdays", "taxable_and_insurable_allowance", "taxable_and_non_insurable_allowance",
            "non_taxable_and_insurable_allowance", "non_taxable_and_non_insurable_allowance", "total_bonus_non_tax",
            "tax_fixed_deduction", "total_deductions", "tax_rate",
        ],
    ))

    content = "".join([block for block in inserts if block])
    Path(args.output).write_text(content, encoding="utf-8")

    print("Generation completed.")
    print(f"  New employees: {len(new_employees)}")
    print(f"  Payroll periods: {len(new_periods)}")
    print(f"  Daily attendances: {len(new_attendances)}")
    print(f"  Attendance summaries: {len(new_summaries)}")
    print(f"  Salary adjustments: {len(new_adjustments)}")
    print(f"  Payslips: {len(new_payslips)}")
    print(f"  Output: {args.output}")


if __name__ == "__main__":
    main()
