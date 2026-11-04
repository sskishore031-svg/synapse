# scratch/make_data_tech.py
import json

def get_tech_catalog():
    # Load 111 and 112 from gen_tech_1_10
    from gen_tech_1_10 import build_tech_subjects
    subs = build_tech_subjects()

    # 113. Cloud Computing & DevOps
    subs.append({
        "id": "SUB_DEVOPS",
        "subject_id": 113,
        "name": "Cloud Computing & DevOps",
        "slug": "cloud-computing-devops",
        "icon_symbol": "☁️",
        "color_hex": "#2563EB",
        "category": "Engineering",
        "description": "Cloud architecture patterns, container orchestration with Kubernetes, CI/CD automation, and Site Reliability Engineering.",
        "is_added": False,
        "chapters": [
            {
                "chapter_id": 1131,
                "chapter_order": 1,
                "title": "Cloud Architecture Models: IaaS, PaaS & Serverless",
                "estimated_minutes": 20,
                "summary": "Shared responsibility model, virtualization hypervisors, multi-tenant isolation, and serverless event-driven computing.",
                "syllabus": [
                    "Cloud service tiers: IaaS, PaaS, SaaS, and FaaS",
                    "Shared responsibility security matrix across cloud tiers",
                    "Virtualization: Type-1 (bare-metal) vs Type-2 hypervisors",
                    "Serverless cold starts, concurrency scaling, and event triggers"
                ],
                "core_formulas": [
                    "Availability A = MTBF / (MTBF + MTTR)",
                    "Downtime_yearly = (1 - A) * 8760 hours",
                    "Cost_serverless = Invocations * Unit_Price + Execution_Duration * Memory_GB * GB_Sec_Rate"
                ],
                "part1_title": "Cloud Service Models & Shared Responsibility",
                "part1_content": "Cloud computing provides on-demand network access to a shared pool of configurable computing resources. Under Infrastructure as a Service (IaaS), providers manage hardware, data centers, and hypervisors, while consumers configure OS, runtimes, and applications. In Platform as a Service (PaaS), providers handle OS patching and middleware. In Function as a Service (FaaS/Serverless), developers deploy stateless event-triggered code without managing server instances.",
                "part2_title": "Availability Metrics & Elastic Scaling",
                "part2_content": "Cloud reliability is measured by Service Level Objectives (SLOs) and High Availability (HA) tiers. A 'four-nines' (99.99%) availability target allows at most 52.6 minutes of downtime per year. High availability architectures distribute workloads across multiple Availability Zones (AZs) using stateless microservices fronted by application load balancers and horizontal auto-scaling groups.",
                "tip": "Remember: In serverless computing, cold starts occur when a function scales from zero instances and must initialize the container runtime.",
                "mcqs": [
                    {
                        "id": 11311, "question_number": 1,
                        "question": "Under the cloud shared responsibility model for an IaaS virtual machine, who is responsible for operating system security patching?",
                        "options": {"A": "The cloud provider exclusively", "B": "The customer / cloud consumer", "C": "The hardware manufacturer", "D": "The ISP"},
                        "correct_option": "B",
                        "explanation": "In IaaS, the cloud provider manages physical hardware, facilities, and hypervisors; the customer is strictly responsible for guest OS configuration, security patches, firewall rules, and application code."
                    },
                    {
                        "id": 11312, "question_number": 2,
                        "question": "What maximum annual downtime is permitted under a 99.99% ('four-nines') Service Level Agreement?",
                        "options": {"A": "Approximately 8.76 hours", "B": "Approximately 52.6 minutes", "C": "Approximately 3.65 days", "D": "Zero minutes"},
                        "correct_option": "B",
                        "explanation": "Annual downtime = (1 - 0.9999) * 365.25 * 24 * 60 minutes = 0.0001 * 525960 ≈ 52.6 minutes."
                    },
                    {
                        "id": 11313, "question_number": 3,
                        "question": "What primary trade-off characterizes serverless Function as a Service (FaaS) compared to long-running containerized servers?",
                        "options": {"A": "Zero idle infrastructure cost versus cold start execution latency during sudden spikes", "B": "Inability to process HTTP requests", "C": "Mandatory manual provisioning of virtual disks", "D": "Single-tenant physical server allocation"},
                        "correct_option": "A",
                        "explanation": "Serverless architectures eliminate cost when idle because instances scale to zero, but cold starts introduce initialization latency when new worker containers must be spawned."
                    },
                    {
                        "id": 11314, "question_number": 4,
                        "question": "What is the primary difference between a Type-1 (bare-metal) and Type-2 hosted hypervisor?",
                        "options": {"A": "Type-1 runs directly on physical server hardware without a host OS", "B": "Type-2 cannot run Linux guests", "C": "Type-1 runs inside a desktop web browser", "D": "Type-2 is faster for enterprise databases"},
                        "correct_option": "A",
                        "explanation": "Type-1 hypervisors (e.g., VMware ESXi, KVM) execute directly on bare metal for enterprise efficiency, whereas Type-2 hypervisors (e.g., VirtualBox) run as applications on top of a host OS."
                    },
                    {
                        "id": 11315, "question_number": 5,
                        "question": "Which architecture pattern decouples synchronous microservice communications to handle traffic spikes smoothly?",
                        "options": {"A": "Synchronous blocking REST calls", "B": "Asynchronous Message Queues (e.g., RabbitMQ, SQS, Kafka)", "C": "Monolithic shared memory", "D": "Hardcoded IP socket connections"},
                        "correct_option": "B",
                        "explanation": "Asynchronous message queues buffer requests during traffic surges, allowing worker services to process events at a controlled rate without cascading overload."
                    }
                ],
                "rag_chunks": [
                    {
                        "chunk_id": "chunk_devops_01",
                        "subject": "Cloud Computing & DevOps",
                        "chapter": "Cloud Architecture Models: IaaS, PaaS & Serverless",
                        "topic": "Cloud Service Models & Availability",
                        "keywords": ["cloud computing", "iaas", "paas", "serverless", "faas", "availability", "shared responsibility", "hypervisor"],
                        "definition": "Cloud computing is the on-demand delivery of IT resources over the internet with pay-as-you-go pricing across IaaS, PaaS, and FaaS service models.",
                        "formula_or_core_rule": "System Availability A = MTBF / (MTBF + MTTR); 99.99% allows ~52.6 minutes annual downtime.",
                        "worked_example": "A system with MTBF of 990 hours and MTTR of 10 hours achieves A = 990 / (990 + 10) = 0.99 (99% availability).",
                        "content": "Modern cloud systems distribute workloads across multiple availability zones using elastic auto-scaling groups and stateless compute layers backed by managed distributed databases."
                    }
                ]
            },
            {
                "chapter_id": 1132,
                "chapter_order": 2,
                "title": "Containerization & Orchestration with Docker & Kubernetes",
                "estimated_minutes": 20,
                "summary": "Linux cgroups, namespaces, OCI container image layers, Kubernetes control plane, Pod scheduling, and Service networking.",
                "syllabus": [
                    "Linux kernel primitives: namespaces (PID, NET, MNT) and cgroups",
                    "Docker container image layers, copy-on-write, and multi-stage builds",
                    "Kubernetes control plane: API Server, etcd, Controller Manager, Scheduler",
                    "Kubernetes Pod networking, Services (ClusterIP, NodePort), and Ingress"
                ],
                "core_formulas": [
                    "Container Isolation = Namespaces (Visibility) + Cgroups (Resource Quotas)",
                    "Image Size = Sum(Layer_Sizes) - Overwritten_Files",
                    "Quorum_etcd = floor(N / 2) + 1 (Fault Tolerance = (N - 1) / 2)"
                ],
                "part1_title": "Linux Kernel Isolation & Container Images",
                "part1_content": "Unlike hardware virtualization, containers share the host Linux kernel. Process isolation is enforced via kernel namespaces, which virtualize system resources (PID for process IDs, NET for network interfaces, MNT for file systems). Control Groups (cgroups) meter and enforce CPU, memory, and I/O resource ceilings. Open Container Initiative (OCI) images utilize immutable layered filesystems with copy-on-write semantics.",
                "part2_title": "Kubernetes Architecture & Declarative Orchestration",
                "part2_content": "Kubernetes is a distributed container orchestration engine. The Control Plane comprises the kube-apiserver (central declarative REST interface), etcd (distributed consensus key-value store), kube-scheduler (assigns pods to nodes based on affinity and resource filters), and controller managers. On worker nodes, kubelet executes pod manifests via container runtime interfaces (CRI), while kube-proxy manages iptables/IPVS service routing rules.",
                "tip": "etcd clusters require an odd number of nodes (3, 5, or 7) to maintain majority quorum (N/2 + 1) during network partitions.",
                "mcqs": [
                    {
                        "id": 11321, "question_number": 1,
                        "question": "Which Linux kernel feature restricts and limits the maximum CPU and RAM consumed by a container process?",
                        "options": {"A": "Namespaces", "B": "Control Groups (cgroups)", "C": "Chroot jail", "D": "SELinux policies exclusively"},
                        "correct_option": "B",
                        "explanation": "cgroups (Control Groups) meter, monitor, and enforce hardware resource limits (CPU quotas, memory caps, block I/O), whereas namespaces govern resource visibility isolation."
                    },
                    {
                        "id": 11322, "question_number": 2,
                        "question": "What is the smallest deployable computing unit in Kubernetes?",
                        "options": {"A": "Container Image", "B": "Pod", "C": "ReplicaSet", "D": "Cluster Node"},
                        "correct_option": "B",
                        "explanation": "A Pod encapsulates one or more co-located containers sharing the same network namespace (localhost, IP address) and storage volumes."
                    },
                    {
                        "id": 11323, "question_number": 3,
                        "question": "In a 5-node etcd cluster, how many node failures can be tolerated while maintaining consensus quorum?",
                        "options": {"A": "1 node", "B": "2 nodes", "C": "3 nodes", "D": "4 nodes"},
                        "correct_option": "B",
                        "explanation": "Quorum requires a strict majority: floor(N/2) + 1 = floor(5/2) + 1 = 3 nodes. Thus, the cluster can tolerate N - Quorum = 5 - 3 = 2 node failures."
                    },
                    {
                        "id": 11324, "question_number": 4,
                        "question": "Which Kubernetes component monitors newly created pods without an assigned node and selects an optimal worker node for them?",
                        "options": {"A": "kube-proxy", "B": "kubelet", "C": "kube-scheduler", "D": "CoreDNS"},
                        "correct_option": "C",
                        "explanation": "The kube-scheduler evaluates resource requests, taints, tolerations, and node affinity rules to assign unassigned pods to suitable worker nodes."
                    },
                    {
                        "id": 11325, "question_number": 5,
                        "question": "What Kubernetes Service type provides an internal virtual IP accessible only within the cluster network?",
                        "options": {"A": "ClusterIP", "B": "NodePort", "C": "LoadBalancer", "D": "ExternalName"},
                        "correct_option": "A",
                        "explanation": "ClusterIP is the default service type that assigns an internal cluster-routable IP address reachable only by components within the cluster."
                    }
                ],
                "rag_chunks": [
                    {
                        "chunk_id": "chunk_devops_02",
                        "subject": "Cloud Computing & DevOps",
                        "chapter": "Containerization & Orchestration with Docker & Kubernetes",
                        "topic": "Containers and Kubernetes Architecture",
                        "keywords": ["kubernetes", "docker", "cgroups", "namespaces", "pod", "etcd", "kube-apiserver", "orchestration"],
                        "definition": "Kubernetes is an open-source container orchestration system for automating application deployment, scaling, and management.",
                        "formula_or_core_rule": "etcd Quorum = floor(N / 2) + 1; Container Isolation = Namespaces + Cgroups.",
                        "worked_example": "A 3-node Kubernetes etcd cluster requires 2 healthy nodes for quorum, tolerating at most 1 node outage.",
                        "content": "Kubernetes reconciles actual state with desired state declared in YAML manifests. Pods share network namespaces, while services provide stable virtual IPs load balanced across ephemeral pod endpoints."
                    }
                ]
            },
            {
                "chapter_id": 1133,
                "chapter_order": 3,
                "title": "CI/CD Pipelines, Infrastructure as Code & SRE",
                "estimated_minutes": 20,
                "summary": "GitOps delivery pipelines, Terraform declarative provisioning, canary/blue-green deployments, and SLI/SLO error budgets.",
                "syllabus": [
                    "CI/CD automation pipelines (Build, Test, Scan, Deploy)",
                    "Infrastructure as Code (IaC): Terraform declarative state and drift detection",
                    "Zero-downtime deployment strategies: Blue-Green vs Canary",
                    "Site Reliability Engineering (SRE): SLI, SLO, SLA, and Error Budget burn rate"
                ],
                "core_formulas": [
                    "Error Budget = 100% - SLO",
                    "Burn Rate = (Observed Error Rate) / (Allowed Error Rate under SLO)",
                    "MTTR = Total Repair Downtime / Total Number of Incidents",
                    "Deployment Frequency * Lead Time for Changes = DORA Throughput Metrics"
                ],
                "part1_title": "Continuous Integration & GitOps Delivery",
                "part1_content": "Continuous Integration (CI) enforces automated testing, security scanning, and container packaging on every version-controlled commit. GitOps uses Git repositories as the single source of truth for declarative infrastructure and applications. Automated agents continuously reconcile live production infrastructure to match declared Git state, rolling back uncommitted configuration drift.",
                "part2_title": "Site Reliability Engineering & Error Budgets",
                "part2_content": "SRE applies software engineering principles to operations. Service Level Indicators (SLIs) measure real-time compliance (e.g., percentage of successful requests). Service Level Objectives (SLOs) define internal target reliability. The Error Budget represents the allowable margin of unreliability (100% - SLO). When error budgets are depleted, feature releases pause to prioritize system stability.",
                "tip": "A Canary deployment routes a tiny fraction (e.g. 5%) of production traffic to the new version before rolling it out globally.",
                "mcqs": [
                    {
                        "id": 11331, "question_number": 1,
                        "question": "If a cloud service establishes an SLO of 99.9% uptime over a 30-day window, what is its available Error Budget?",
                        "options": {"A": "0.1% allowable downtime (approx 43.2 minutes)", "B": "1.0% downtime", "C": "Zero downtime", "D": "10 hours"},
                        "correct_option": "A",
                        "explanation": "Error Budget = 100% - SLO = 100% - 99.9% = 0.1%. In a 30-day period (43,200 minutes), 0.1% equals 43.2 minutes of allowable downtime."
                    },
                    {
                        "id": 11332, "question_number": 2,
                        "question": "What is the primary difference between Blue-Green and Canary deployment strategies?",
                        "options": {"A": "Blue-Green switches 100% of traffic instantly between duplicate environments, whereas Canary routes a small traffic percentage incrementally to test in production", "B": "Canary deployments cannot be rolled back", "C": "Blue-Green requires no load balancer", "D": "Canary deployments require manual compilation"},
                        "correct_option": "A",
                        "explanation": "Blue-Green maintains two identical environments and cuts traffic over instantly via load balancer, whereas Canary routes a small percentage (e.g., 2-5%) to detect bugs before widespread rollout."
                    },
                    {
                        "id": 11333, "question_number": 3,
                        "question": "What is configuration drift in Infrastructure as Code (IaC)?",
                        "options": {"A": "Discrepancy between actual live cloud infrastructure and the declared code configuration", "B": "Network packets arriving out of order", "C": "DNS records expiring", "D": "Slow disk drive performance"},
                        "correct_option": "A",
                        "explanation": "Configuration drift occurs when manual console changes or out-of-band updates cause live infrastructure to diverge from the declarative Terraform or GitOps code repository."
                    },
                    {
                        "id": 11334, "question_number": 4,
                        "question": "Which metric is one of the four foundational DORA (DevOps Research and Assessment) metrics?",
                        "options": {"A": "Lead Time for Changes", "B": "Lines of code written per day", "C": "Number of open Jira tickets", "D": "CPU clock speed"},
                        "correct_option": "A",
                        "explanation": "The 4 DORA metrics are Deployment Frequency, Lead Time for Changes, Mean Time to Restore (MTTR), and Change Failure Rate."
                    },
                    {
                        "id": 11335, "question_number": 5,
                        "question": "What action is typically mandated in SRE when a service exhausts its Error Budget for the month?",
                        "options": {"A": "Immediate freeze on non-critical feature releases to focus developer efforts on reliability and technical debt", "B": "Filing a lawsuit against the cloud vendor", "C": "Doubling marketing budget", "D": "Terminating all monitoring alerts"},
                        "correct_option": "A",
                        "explanation": "When an error budget is burned, feature releases are halted so engineering efforts can focus entirely on reliability engineering, bug fixes, and infrastructure stabilization."
                    }
                ],
                "rag_chunks": [
                    {
                        "chunk_id": "chunk_devops_03",
                        "subject": "Cloud Computing & DevOps",
                        "chapter": "CI/CD Pipelines, Infrastructure as Code & SRE",
                        "topic": "CI/CD, IaC, and SRE Error Budgets",
                        "keywords": ["devops", "ci/cd", "gitops", "terraform", "sre", "slo", "sli", "error budget", "canary deployment"],
                        "definition": "Site Reliability Engineering (SRE) utilizes software engineering practices to automate operations and govern release velocity using quantitative Error Budgets.",
                        "formula_or_core_rule": "Error Budget = 100% - SLO; Burn Rate = Observed Failure Rate / Allowed Budget Rate.",
                        "worked_example": "For a 99.9% SLO with 1 million requests, allowed failed requests = 1,000 requests per month.",
                        "content": "Modern DevOps pipelines implement automated CI testing, declarative Terraform infrastructure provisioning, and progressive canary deployments regulated by SRE error budgets."
                    }
                ]
            }
        ]
    })

    # Return completed subjects so far
    return subs

