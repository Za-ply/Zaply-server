variable "name_prefix" {
  description = "리소스 접두사"
}

variable "vpc_no" {
  description = "VPC 번호"
}

variable "subnet_id" {
  description = "Redis를 배치할 Subnet ID"
}

variable "acg_rules" {
  description = "Redis 접근용 ACG 규칙 리스트"
  type = list(object({
    protocol  = string
    from_port = number
    to_port   = number
    cidr      = string
  }))
  default = [
    { protocol = "TCP", from_port = 6379, to_port = 6379, cidr = "0.0.0.0/0" },
  ]
}
