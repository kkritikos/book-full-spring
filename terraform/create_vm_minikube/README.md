This Terraform project enables to create a simulated Kubernetes cluster via minikube and to deploy in this cluster the 
backend of a book management application. The simulated cluster is created in a VM instance at the AWS cloud. The ID & 
public IP of the instance are returned as output. The user, once the project is run and the plan applied, needs to
connect to the VM instance, run 'minikube tunnel -p test &' and then check whether the application deployment is successful.  