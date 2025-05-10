# 1) Redis 전용 ACG 생성
resource "ncloud_access_control_group" "redis_acg" {
  name        = "${var.name_prefix}-redis-acg"
  description = "Redis 접근 제어 그룹"
  vpc_no      = var.vpc_no
}

# 2) ACG 규칙
resource "ncloud_access_control_group_rule" "redis_rules" {
  for_each = { for idx, rule in var.acg_rules : idx => rule }

  acg_no    = ncloud_access_control_group.redis_acg.acg_no
  protocol  = each.value.protocol
  from_port = each.value.from_port
  to_port   = each.value.to_port
  cidr      = each.value.cidr
  direction = "INGRESS"
}

# 3) Redis 클러스터 (예시 리소스 타입; 실제 리소스명 확인 필요)
resource "ncloud_redis_cluster" "this" {
  cluster_name        = "${var.name_prefix}-redis"
  vpc_no              = var.vpc_no
  subnet_id_list      = [var.subnet_id]
  acg_no              = ncloud_access_control_group.redis_acg.acg_no
  # node_count, node_type, engine_version 등 추가 설정
}
