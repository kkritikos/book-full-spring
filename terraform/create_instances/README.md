This root module enables to create a deployment for the book management micro-service as well as to destroy this deployment, if it is not
needed any more. The deployment includes creating one VM instance for the MySQL DBMS component and two VM instances for the RESTful service
component. The two VM instances of the latter component are registered in a load balancer. 
To create the micro-service deployment, you need to execute terraform init followed by terraform destroy.
To destroy the deployment, you need to execute terraform destroy.
It must be noted that the images from which the VM instances are created should have been already constructed by using the create_images 
sibling Terraform project.  