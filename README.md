# Excel2Blockchain

In this demo data from an Excelsheet is pushed into a Smart Contract deployed on a Ethereum chain. The demo includes compilation and deployment of the Smart Contract. The chain is configured in the Java source code for simplicity, samples for Ganache/TestRPC and Rinkeby/Infura are included.

[docs/img/overview.png]

# Prerequisites

* Ganache (or any other Ethereum Node or Infura)
* Java 8+
* Maven
* optional: Git

# Setup

* Clone project with `git clone` or download the project as a compressed file

# Run

* Start Ganache with mnemonic `candy maple cake sugar pudding cream honey rich smooth crumble sweet treat`
* Build project with `mvn clean package`
* Change to directory target and start application with `java -jar `
