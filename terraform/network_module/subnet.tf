resource "ncloud_subnet" "public" {
    name            = "${var.name_prefix}-public"
    vpc_no          = ncloud_vpc.this.vpc_no
    subnet          = cidrsubnet(ncloud_vpc.this.ipv4_cidr_block, 8, 0) # "10.0.0.0/24"
    zone            = var.zone
    network_acl_no  = ncloud_vpc.this.default_network_acl_no
    subnet_type     = "PUBLIC"
}
