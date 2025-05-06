# Dummie infrastructure file

# Path: infrastructure/main.tf

# Configure the AWS provider
provider "aws" {
  region = "us-east-1"  # Replace with your desired AWS region
}

# Create a new VPC for the ECS service
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"  # IP range for the VPC
}

# Create a public subnet in the VPC
resource "aws_subnet" "main" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.1.0/24"  # IP range for the subnet
  availability_zone = "us-east-1a"   # You can update this as needed
}

# Create an ECS Cluster to run the Fargate tasks
resource "aws_ecs_cluster" "transaction_service" {
  name = "transaction-service-cluster"
}

# IAM Role for the ECS Task to allow pulling images, logging, etc.
resource "aws_iam_role" "ecs_task_exec" {
  name = "ecsTaskExecutionRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

# Attach the built-in policy for ECS Task Execution to the role
resource "aws_iam_role_policy_attachment" "ecs_task_exec_policy" {
  role       = aws_iam_role.ecs_task_exec.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Define the ECS Task Definition for your Spring Boot app
resource "aws_ecs_task_definition" "transaction_service" {
  family                   = "transaction-service-task"      # Grouping name for multiple versions
  requires_compatibilities = ["FARGATE"]                     # Run on Fargate
  network_mode             = "awsvpc"                        # Required for Fargate
  cpu                      = "256"                           # 0.25 vCPU
  memory                   = "512"                           # 512 MB RAM
  execution_role_arn       = aws_iam_role.ecs_task_exec.arn  # Use the IAM role above

  container_definitions = jsonencode([
    {
      name      = "transaction-service"
      image     = "your-dockerhub-user/transaction-service-app:latest"  # Replace with actual image
      portMappings = [
        {
          containerPort = 8080
        }
      ]
      environment = [
        { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://db:5432/transaction_service_db" },
        { name = "SPRING_DATASOURCE_USERNAME", value = "postgres" },
        { name = "SPRING_DATASOURCE_PASSWORD", value = "root" }
      ]
    }
  ])
}

# Create a security group to allow inbound traffic on port 8080
resource "aws_security_group" "ecs_service" {
  name        = "transaction_service_sg"
  description = "Allow inbound HTTP"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]  # Allow access from anywhere (can restrict for prod)
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Define the ECS Service to run and manage the task
resource "aws_ecs_service" "transaction_service" {
  name            = "transaction-service"
  cluster         = aws_ecs_cluster.transaction_service.id
  task_definition = aws_ecs_task_definition.transaction_service.arn
  launch_type     = "FARGATE"
  desired_count   = 1  # Number of instances to run

  network_configuration {
    subnets         = [aws_subnet.main.id]
    assign_public_ip = true  # Assign a public IP (optional for dev)
    security_groups = [aws_security_group.ecs_service.id]
  }
}
