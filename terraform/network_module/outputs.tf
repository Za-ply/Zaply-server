output "vpc_no" {
    description = "생성된 VPC 번호"
    value       = ncloud_vpc.this.vpc_no
}
output "subnet_public_id" {
    description = "생성된 퍼블랫 서브넷 ID"
    value       = ncloud_subnet.public.id
}
