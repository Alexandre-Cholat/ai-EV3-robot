# ai-EV3-robot

## 📝 Project Overview

This project focuses on the development and implementation of an artificial intelligence algorithm for a **LEGO Mindstorms EV3 robot** to autonomously collect and deposit pucks (palets) into a designated goal area within a dynamic environment.

The solution is implemented using the **LeJOS EV3 Java Platform**.

## ✨ Key Features & Strategy Highlights

Our strategy prioritizes robustness and adaptability in a chaotic environment with imprecise motors and light-sensitive sensors.

*   **Autonomous Control:** The robot's behavior is modeled using a **simple State Machine Automaton** with four main states:
    1.  Go to the center of the playing area.
    2.  Search for a puck (by rotation and sensor analysis).
    3.  Approach and grab the puck.
    4.  Return to the adverse goal to deposit the puck.
*   **Navigation Strategy:** A novel approach was adopted to minimize reliance on precise position knowledge. The robot recalibrates its position using the *align* method, which leverages the ultrasonic sensor to center itself relative to a wall.
*   **Detection Mechanism:** Pucks are detected using progressive rotation and the **ultrasonic sensor**. An abrupt change in distance (discontinuity) indicates a puck. A post-detection adjustment of 15° is applied to ensure accurate alignment before approach.


## 🛠️ Technical Stack & Hardware

*   **Platform:** LEGO Mindstorms EV3
*   **Programming Language:** Java (via the LeJOS EV3 API)
*   **Sensors Utilized:**
    *   Ultrasonic Sensor (Primary for navigation and detection)
    *   Tactile Sensors
*   **Actuators:**
    *   Two motors for movement.
    *   One motor for the gripper mechanism.

## 👥 Team

**Members:**
*   Adélie Bardagi
*   Alexandre Cholat
*   Yelli Coulibaly

**Supervisor:** Mr Damien Pellier

## 📂 Report & Documentation

The full project report, detailing the problem analysis, technical choices, challenges encountered, and collaborative process, is included in this repository.

*   [**Full Project Report**](https://github.com/Alexandre-Cholat/ai-EV3-robot/blob/main/Rapport%20de%20projet%20IA%20s5%20MIASHS.pdf)

## 💡 Potential Future Improvements

*   Implement a **precise positioning class** to map the environment and increase search efficiency.
*   Improve puck detection reliability by using the static dimensions of the puck and **trigonometric calculation** with angle-between-discontinuities.
*   Optimize the initial algorithm with a 'start-of-game' state to leverage complete information for maximum speed.

***

#### This work was supported by the French government, under the management of the National Research Agency (ANR), as part of the "Investments for the Future" program with the reference ANR-22-CMAS-0005.

```
