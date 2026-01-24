# Anagno: Intra-Campus Logistics Portal

**Anagno** is a high-performance, JavaFX-based logistics ecosystem engineered to streamline intra-campus supply chains. By digitizing the workflow between local merchants and student couriers, the system provides a unified infrastructure for inventory control, real-time dispatch tracking, and last-mile fulfillment.

---

## 🤖 AI-Augmented Development
This project showcases a modern **Human-AI Collaborative workflow**. AI tools were integrated into the development lifecycle to achieve:

* **UI/UX Modernization:** Accelerated the transition from legacy JavaFX Modena styles to a custom **Indigo & Slate** design system using AI-generated CSS components.
* **System Architecture:** Refined the Model-View-Controller (MVC) separation and Role-Based Access Control (RBAC) logic through AI-assisted code reviews.

---

## 🚀 Key Modules

* **🏪 Merchant Ledger:** A comprehensive inventory management interface allowing vendors to track stock, prices, and product availability in real-time.
* **🚚 Courier Dispatch:** A dedicated dashboard for student delivery personnel featuring one-tap status updates and assignment tracking.
* **🔐 Secure Authentication:** A robust login gateway that intelligently routes users to their respective dashboards based on database-driven roles.
* **🖥️ Modern Desktop UX:** An undecorated, draggable interface featuring rounded aesthetics, soft shadows, and responsive FXML layouts.

---

## 🛠️ Technical Stack

* **Language:** Java 17+
* **Framework:** JavaFX (FXML)
* **Architecture:** MVC Pattern (Model-View-Controller)
* **Styling:** Custom CSS (Web-inspired design system)
* **Persistence:** SQL-based User & Inventory Management


---

## 📂 Project Structure

```text
src/anagno/
 ├── controller/   # UI Event Handling & Business Logic
 ├── model/        # Data Structures & Database Access
 ├── utils/        # Session & Connection Management
 └── view/         # FXML Layouts & CSS Design System
