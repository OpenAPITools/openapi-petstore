terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 3.20.0"
    }

    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = ">= 2.0.1"
    }
  }
}

data "terraform_remote_state" "eks" {
  backend = "remote"

  config = {
    organization = "joshuayeungzuhlke"
    workspaces = {
      name = "openapi-petstore-devops"
    }
  }
}

# Retrieve EKS cluster information
provider "aws" {
  region = data.terraform_remote_state.eks.outputs.region
}

data "aws_eks_cluster" "cluster" {
  name = data.terraform_remote_state.eks.outputs.cluster_id
}

provider "kubernetes" {
  host                   = data.aws_eks_cluster.cluster.endpoint
  cluster_ca_certificate = base64decode(data.aws_eks_cluster.cluster.certificate_authority.0.data)
  exec {
    api_version = "client.authentication.k8s.io/v1"
    command     = "aws"
    args = [
      "eks",
      "get-token",
      "--cluster-name",
      data.aws_eks_cluster.cluster.name
    ]
  }
}

resource "kubernetes_deployment" "openapi_petstore" {
  metadata {
    name = "scalable-openapi-petstore-example"
    labels = {
      App = "ScalableOpenApiPetstoreExample"
    }
  }

  spec {
    replicas = 4
    selector {
      match_labels = {
        App = "ScalableOpenApiPetstoreExample"
      }
    }
    template {
      metadata {
        labels = {
          App = "ScalableOpenApiPetstoreExample"
        }
      }
      spec {
        container {
          image = "openapitools/openapi-petstore"
          name  = "example"

          port {
            container_port = 8080
          }

          resources {
            limits = {
              cpu    = "0.5"
              memory = "512Mi"
            }
            requests = {
              cpu    = "250m"
              memory = "50Mi"
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "openapi_petstore" {
  metadata {
    name = "openapi-petstore-example"
  }
  spec {
    selector = {
      App = kubernetes_deployment.openapi_petstore.spec.0.template.0.metadata[0].labels.App
    }
    port {
      port        = 80
      target_port = 8080
    }

    type = "LoadBalancer"
  }
}

output "lb_ip" {
  value = kubernetes_service.openapi_petstore.status.0.load_balancer.0.ingress.0.hostname
}

