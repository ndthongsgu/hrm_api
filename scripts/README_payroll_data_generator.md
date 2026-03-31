# Payroll Data Generator

## Muc tieu
Script `generate_payroll_data.py` sinh du lieu lon co kiem soat va tinh payslip theo dung cong thuc hien tai trong BE (`PayslipServiceImpl.generateForEmployee`).

## Dau vao
- SQL dump goc (mac dinh: `db.sql`)
- Seed de tai lap du lieu
- So luong ky luong can sinh
- So luong nhan vien muc tieu

## Dau ra
- File SQL insert bulk (mac dinh: `generated_payroll_data.sql`)
- Bao cao tong hop so dong duoc sinh

## Chay script
Neu may co Python:

python scripts/generate_payroll_data.py --input db.sql --output generated_payroll_data.sql --employees 1000 --periods 6 --seed 42 --validate-existing

Neu dang dung conda:

C:/ProgramData/miniconda3/Scripts/conda.exe run -p C:\ProgramData\miniconda3 --no-capture-output python scripts/generate_payroll_data.py --input db.sql --output generated_payroll_data.sql --employees 1000 --periods 6 --seed 42 --validate-existing

## Ghi chu logic
- Tinh cong thuc tax/insurance/deduction theo service BE hien tai.
- Chia luong theo cong: gross * actualDays / standardDays.
- Tinh overtime theo DailyAttendanceRepository (chi status OK va so gio > gio ca).
- Tinh salary adjustment giu dung hanh vi service hien tai.

## Noi dung du lieu sinh
- employees (nhan vien moi neu can)
- contracts
- contract_allowances
- dependents
- payroll_periods
- daily_attendances
- attendance_summaries
- salary_adjustments
- payslips
