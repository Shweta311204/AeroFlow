<div align="center">

# ✈ AeroFlow Pro

### Airline Flight Schedule Manager

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge\&logo=openjdk\&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-1876DC?style=for-the-badge\&logo=java\&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-22C55E?style=for-the-badge)

A polished desktop application for managing airline flight schedules — built entirely with **Java Swing**.

</div>

---

## 📁 Project Structure

```
AeroFlow/
│
└── src/
    ├── AeroFlowApp.java
    ├── Flight.java
    ├── FlightTableModel.java
    ├── FlightComparators.java
    ├── AddFlightDialog.java
    └── FilterDialog.java
```

---

## ✨ Features

* ✈ Flight log with sorting & filtering
* 🔍 Live search
* ➕ Add / ✏ Edit / ❌ Delete flights
* ⏱ Sort by time or price
* 🔽 Filter by price
* 📊 Dashboard summary

---

## ⚙️ Prerequisites — Install Java (JDK 17 ONLY)

This project **requires JDK 17**.

### 🔗 Download (Oracle Only)

https://www.oracle.com/java/technologies/downloads/

* Download **JDK 17 (LTS)**
* Install normally
* Ensure:

  * `JAVA_HOME` is set
  * `PATH` includes JDK `/bin`

---

### ✅ Verify Installation

```bash
java -version
javac -version
```

✔️ Must show **17**

---

## ⚠️ If you have an older Java version

### Windows Fix

1. Install JDK 17 from Oracle
2. Open **Environment Variables**
3. Update:

   * `JAVA_HOME` → JDK 17 path
   * `Path` → include JDK 17 `/bin`
4. Restart terminal

---

## 💻 GitHub Codespaces Setup (IMPORTANT)

Codespaces may not use Java 17 by default.

### ✅ Step 1: Check version

```bash
java -version
```

---

### ❌ If NOT Java 17 → Install it

```bash
sudo apt update
sudo apt install openjdk-17-jdk -y
```

---

### 🔄 Step 2: Set Java 17 as default

```bash
sudo update-alternatives --config java
sudo update-alternatives --config javac
```

Select Java 17.

---

### ✅ Step 3: Verify

```bash
java -version
javac -version
```

---

## 🚀 How to Run

### Windows / Mac / Linux

```bash
cd AeroFlow
javac -d out -sourcepath src src/AeroFlowApp.java
java -cp out AeroFlowApp
```

---

## 🛠 Troubleshooting

| Problem                        | Fix                                 |
| ------------------------------ | ----------------------------------- |
| `javac not found`              | Install full JDK 17                 |
| `UnsupportedClassVersionError` | Java version too old → update to 17 |
| GUI not opening in Codespaces  | Use Xvfb or run locally             |

---

## 🔗 Resources

* Oracle JDK: https://www.oracle.com/java/technologies/downloads/
* Java Swing Docs: https://docs.oracle.com/javase/tutorial/uiswing/

---

<div align="center">
Built with ☕ Java Swing • Pure Java SE • No external dependencies
</div>
