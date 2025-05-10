output "cluster_no" {
  description = "Redis Cluster 번호"
  value       = ncloud_redis_cluster.this.cluster_no
}

output "endpoint" {
  description = "Redis 엔드포인트 주소"
  value       = ncloud_redis_cluster.this.endpoint
}
