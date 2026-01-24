# AIUP Task Manager

This repository contains a **case study application** used in the book *Spec-driven Development with the AI Unified
Process (AIUP)*.

The goal of this project is not to build a feature-rich task manager, but to show **how specifications, code, and tests
stay in sync** when using a spec-driven approach with AI assistance.

## Purpose of This Repository

This project demonstrates:

* how system behavior is defined using **structured specifications**
* how **System Use Cases** act as executable contracts
* how code and tests are derived from specs
* how AI is used to **update existing code** when specifications change, instead of regenerating everything

The repository is intentionally small and focused.

## Where the Specifications Live

All specifications are located in the `/docs` directory.

```
/docs
├── vision.md
├── requirements.md
├── entity-model.md
└── use-cases
    ├── UC-01-create-task.md
    ├── UC-02-view-task-list.md
    └── ...
```

The `/docs` folder is the **single source of truth** for system behavior.

Code and tests must always reflect what is defined there.

## Repository Structure

```
aiup-task-manager
├── docs                # All specifications (source of truth)
├── src
│   ├── main            # Application code
│   └── test            # Automated tests derived from use cases
├── README.md
└── pom.xml
```

* `/docs`
  Contains vision, requirements, entity model, and system use cases.

* `/src/main`
  Implementation derived from the specifications.

* `/src/test`
  Tests that verify the behavior defined in the system use cases.

## Workflow Overview

1. Start with a short **vision**
2. Create or update **requirements** using AIUP commands
3. Define **System Use Cases** with main and alternative flows
4. Generate or update code and tests from these specifications
5. When specs change, synchronize code and tests instead of rewriting them

This workflow is explained step by step in the book.

## What This Project Is Not

* Not a production-ready task manager
* Not a framework showcase
* Not a full reference architecture

It is a **didactic example** designed to make spec-driven development concrete and understandable.
