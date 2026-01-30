# Intelligent Energy Grid Simulation

## Overview
A JADE-based multi agent system that simulates a decentralized smart energy grid in real time. It includes **Consumer agents** which autonomously negotiate with **Producer agents** to purchase electricity using the FIPA Contract Net Protocol. The **Grid Manager agent** monitors the system state and logs statistics in real time using a thread-safe buffering mechanism.


## Installation Steps
### Prerequisites:
  - Java Development Kit (JDK) 11 (or higher)
  - IntelliJ IDEA/ Eclipse
  - JADE Framework: the JADE `.jar` files can be downloaded from their official site

 ### Setup:
   - Download/Clone repository
   - Open project in preferred IDE
   - Add the JADE `.jar` files to the Project Structure > Libraries


## Usage Instructions
1.  Navigate to `src/org/example/Main.java`.
2.  Run the `Main` class.
    * This will automatically launch the JADE Container and start the GUI.
    * It initializes 1 Manager, 5 Producers, and 3 Consumers.
3.  Observe the output:
    * Check the Console to see the negotiation logs (Proposals, Refusals, and Acceptances).
    * Watch the **Blue Dashboard Box** (`[GRID STATISTICS UPDATED]`) appear every 3 seconds with the latest transaction summaries.


## Technologies Used
* **Java**
* **JADE Framework** 
* **Multi-Threading** (Synchronized Lists for Buffering)
