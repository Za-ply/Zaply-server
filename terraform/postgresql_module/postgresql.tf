# 1) Postgres 전용 ACG 생성
resource "ncloud_access_control_group" "pg_acg" {
  name        = "${var.name_prefix}-pg-acg"
  description = "PostgreSQL 접근 제어 그룹"
  vpc_no      = var.vpc_no
}

# 2) 기본 ACG 룰 (예: 5432/TCP 오픈)
resource "ncloud_access_control_group_rule" "pg_rule" {
  acg_no    = ncloud_access_control_group.pg_acg.acg_no
  protocol  = "TCP"
  from_port = 5432
  to_port   = 5432
  cidr      = "0.0.0.0/0"
  direction = "INGRESS"
}

# 3) PostgreSQL 인스턴스 (예시 리소스 타입; 실제 리소스명 확인 필요)
resource "ncloud_db_instance" "postgresql" {
  instance_name     = "${var.name_prefix}-postgresql"
  engine            = "POSTGRESQL"
  engine_version    = var.engine_version
  vpc_no            = var.vpc_no
  subnet_id_list    = [var.subnet_id]
  acg_no            = ncloud_access_control_group.pg_acg.acg_no
  storage_size      = var.storage_size
  master_username   = var.username
  master_password   = var.password
  # backup_retention, multi_az 등 추가 옵션
}
