variable "name_prefix" {
    description = "리소스 접두사"
}
variable "vpc_cidr" {
    description = "VPC CIDR 블록"
    default     = "10.0.0.0/16"
}
variable "zone" {
    description = "Public Subnet 가용 영역"
    default     = "KR-2"
}
