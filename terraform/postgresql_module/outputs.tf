output "instance_no" {
  description = "PostgreSQL 인스턴스 번호"
  value       = ncloud_db_instance.postgresql.instance_no
}

output "endpoint" {
  description = "PostgreSQL 엔드포인트"
  value       = ncloud_db_instance.postgresql.endpoint
}
