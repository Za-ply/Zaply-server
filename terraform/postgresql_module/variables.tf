variable "name_prefix" {
  description = "리소스 접두사"
}

variable "vpc_no" {
  description = "VPC 번호"
}

variable "subnet_id" {
  description = "PostgreSQL을 배치할 Subnet ID"
}

variable "engine_version" {
  description = "PostgreSQL 엔진 버전"
  default     = "13"
}

variable "storage_size" {
  description = "스토리지 크기 (GB)"
  default     = 20
}

variable "username" {
  description = "DB 관리자 계정명"
  default     = "admin"
}

variable "password" {
  description = "DB 관리자 비밀번호"
  sensitive   = true
}
