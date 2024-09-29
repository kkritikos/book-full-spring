data "aws_iam_policy_document" "lb_assume_role_policy" {
  statement {
    principals {
      type        = "Federated"
      identifiers = [var.oidc_arn] # Use the OIDC URL from EKS module
    }

    actions = ["sts:AssumeRoleWithWebIdentity"]

    condition {
      test     = "StringEquals"
      variable = "${var.oidc_url}:sub"
      values   = ["system:serviceaccount:kube-system:aws-load-balancer-controller"]
    }
    
    condition {
      test     = "StringEquals"
      variable = "${var.oidc_url}:aud"
      values   = ["sts.amazonaws.com"]
    }
  }
}  
  
resource "aws_iam_role" "lb_controller_role" {
  name               = "eks-lb-controller-role"
  assume_role_policy = data.aws_iam_policy_document.lb_assume_role_policy.json
}

resource "aws_iam_policy" "lb_controller_policy" {
  name        = "eks-lb-controller-policy"
  description = "Policy for EKS Load Balancer Controller"
  policy      = file("./modules/add_ons/policies/lb-controller-policy.json")
}

resource "aws_iam_role_policy_attachment" "lb_controller_policy_attachment" {
  role       = aws_iam_role.lb_controller_role.name
  policy_arn = aws_iam_policy.lb_controller_policy.arn
}

resource "kubernetes_service_account" "aws_load_balancer_controller" {
  metadata {
    name      = "aws-load-balancer-controller"
    namespace = "kube-system"
    annotations = {
      "eks.amazonaws.com/role-arn" = aws_iam_role.lb_controller_role.arn
    }
  }
}

resource "helm_release" "cert_manager" {
  name       = "cert-manager"
  namespace  = "cert-manager"
  create_namespace = true
  repository = "https://charts.jetstack.io"
  chart      = "cert-manager"
  version    = "v1.12.0"

  set {
    name  = "installCRDs"
    value = "true"
  }
}

resource "helm_release" "aws_load_balancer_controller" {
  name       = "aws-load-balancer-controller"
  repository = "https://aws.github.io/eks-charts"
  chart      = "aws-load-balancer-controller"
  namespace  = "kube-system"
  version    = "1.5.1"  # Ensure it's compatible with your EKS version

  set {
    name  = "clusterName"
    value = var.cluster_name
  }

  set {
    name  = "serviceAccount.create"
    value = "false"
  }

  set {
    name  = "serviceAccount.name"
    value = kubernetes_service_account.aws_load_balancer_controller.metadata[0].name
  }

  set {
    name  = "region"
    value = var.region
  }

  depends_on = [
    kubernetes_service_account.aws_load_balancer_controller
  ]
}

data "aws_iam_policy_document" "ebs_assume_role_policy" {
  statement {
    principals {
      type        = "Federated"
      identifiers = [var.oidc_arn] # Use the OIDC URL from EKS module
    }

    actions = ["sts:AssumeRoleWithWebIdentity"]

    condition {
      test     = "StringEquals"
      variable = "${var.oidc_url}:sub"
      values   = ["system:serviceaccount:kube-system:ebs-csi-controller-sa"]
    }
    
    condition {
      test     = "StringEquals"
      variable = "${var.oidc_url}:aud"
      values   = ["sts.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ebs_csi_role" {
  name               = "eks-ebs-csi-driver-role"
  assume_role_policy = data.aws_iam_policy_document.ebs_assume_role_policy.json
}

resource "aws_iam_policy" "ebs_csi_policy" {
  name        = "eks-ebs-csi-policy"
  description = "Policy for EKS EBS CSI Driver"
  policy      = file("./modules/add_ons/policies/ebs-csi-policy.json")
}

resource "aws_iam_role_policy_attachment" "ebs_csi_policy_attachment" {
  role       = aws_iam_role.ebs_csi_role.name
  policy_arn = aws_iam_policy.ebs_csi_policy.arn
}

resource "aws_eks_addon" "ebs_csi_driver" {
  cluster_name = var.cluster_name
  addon_name   = "aws-ebs-csi-driver"
  service_account_role_arn = aws_iam_role.ebs_csi_role.arn  # Update with your IAM role ARN
}