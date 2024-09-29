resource "kubernetes_namespace" "book" {
  metadata {
    name = "book"
  }
}

resource "kubernetes_manifest" "app_manifests" {
  for_each = fileset("../../kubernetes/eks", "*.yaml")

  manifest = yamldecode(file("../../kubernetes/eks/${each.key}"))
}