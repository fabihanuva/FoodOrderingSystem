# MU Food Corner 

A desktop food-ordering application for a campus food counter, built with **Java Swing**. Customers can browse the menu, build a cart, apply promo codes, and place orders; staff can review order history and manage the menu through a password-protected admin panel.

---

## Features

- **Shop tab** – live menu with quantity spinners, running subtotal/total, promo code support (`MU50`, `OFF10`), order placement, cart reset, and receipt export to a text file.
- **History tab** – tabular view of all past orders with the ability to open a full receipt and update an order's status (Pending → Cooking → Out for Delivery → Delivered → Cancelled).
- **Admin tab** – password-gated menu management (add/update items, delete with confirmation).
- **Persistence** – orders and menu data survive restarts via Java object serialization (`orders.ser`, `menu.ser`).

## Tech Stack

| Component | Choice |
|---|---|
| Language | Java (compiled with source/target 25, see `pom.xml`) |
| UI Toolkit | Java Swing (AWT) |
| Build Tool | Maven |
| Testing | JUnit 5 (Jupiter) |
| Persistence | Java Serialization (flat `.ser` files) |

## Project Structure

```
MUFoodCorner/
├── pom.xml
├── src/
│   ├── main/java/MUFoodCornerAdvanced.java   # entry point + entire application
│   └── test/java/MenuItemTest.java           # unit tests for the MenuItem model
├── orders.ser                                 # generated at runtime
└── menu.ser                                   # generated at runtime
```

## Getting Started

**Prerequisites:** JDK matching (or newer than) the version set in `pom.xml`, and Maven.

```bash
# Compile and run the test suite
mvn test

# Compile and launch the application
mvn compile exec:java -Dexec.mainClass="MUFoodCornerAdvanced"
```

If the `exec` plugin isn't pre-configured, Maven will resolve it automatically on first run, or you can build manually:

```bash
javac -d out src/main/java/MUFoodCornerAdvanced.java
java -cp out MUFoodCornerAdvanced
```

---

## Design Patterns Used

The application is a single-window Swing tool, so it doesn't need a large architecture — but a handful of classic design patterns are used deliberately to keep the UI code consistent, decoupled, and easy to extend. Each one is documented below with **where** it appears, **what problem it solves**, and **why it was the right choice** here.

### 1. Factory Method Pattern

**Where:** `createBtn()`, `createStyledCard()`, `createStyledInput()`, `createInputWrapper()`, and the panel builders `createOrderPanel()`, `createHistoryPanel()`, `createAdminPanel()`.

**Problem it solves:** Every Swing component in this app (a card, a button, a text field) needs the same recurring setup — fonts, colors, borders, hover behavior, cursor style. Without a factory, that boilerplate would be copy-pasted at every call site, and any visual change (say, a new accent color or corner radius) would require editing dozens of places.

**Why it fits:** Each `create*()` method acts as a factory that hides construction details behind a simple call (e.g. `createBtn("PLACE ORDER NOW", ACCENT)`) and returns a fully-styled, ready-to-use component. This centralizes styling logic, guarantees visual consistency across all three tabs, and means a single edit propagates everywhere the factory is used.

### 2. Observer Pattern

**Where:** Every `addActionListener`, `addChangeListener`, `addKeyListener`, and `addMouseListener` call — for example, the quantity `JSpinner`'s change listener recalculating the total, `promoField`'s key listener re-pricing the cart as the user types, and the tabbed pane's change listener gating access to the Admin tab.

**Problem it solves:** The UI needs to react to user-driven events (typing, clicking, adjusting a spinner) without the components themselves knowing anything about business logic like pricing or order confirmation. Some mechanism is needed to notify interested code when state changes, while keeping the widget and the logic loosely coupled.

**Why it fits:** Java Swing's event-listener model *is* the Observer pattern at the language level — a widget (the *subject*) maintains a list of listeners (the *observers*) and notifies them when an event occurs. The app leans directly on this instead of polling component state, which keeps `confirmOrder()`, `calculateTotal()`, and the admin password gate reactive and independent of the components that trigger them.

### 3. Decorator Pattern

**Where:** The custom `RoundedBorder` class (extends `AbstractBorder`) combined at runtime with `EmptyBorder` via `CompoundBorder` — e.g. in `createStyledCard()`:
```java
p.setBorder(new CompoundBorder(
    new RoundedBorder(20, new Color(230, 230, 230));
    new EmptyBorder(20, 20, 20, 20);
));
```
and similarly in `createStyledInput()` and on `summaryArea`.

**Problem it solves:** Cards, inputs, and text areas all need a rounded outline *and* internal padding, but those two visual behaviors are independent and reusable in different combinations elsewhere. Hard-coding a single "rounded + padded" border class for every component would duplicate code and make future variations (e.g. rounded without padding) awkward.

**Why it fits:** `CompoundBorder` is Swing's built-in Decorator: it wraps one `Border` implementation around another so behaviors can be layered without subclassing. `RoundedBorder` only knows how to draw a rounded outline; `EmptyBorder` only knows how to add spacing. Composing them at the call site keeps each concern single-purpose while letting every component mix and match border styles freely.

### 4. Model–View Separation (light MVC)

**Where:** `MenuItem` and `Order` are plain, UI-agnostic `Serializable` data classes. The History and Admin tables use `DefaultTableModel` (the data) driving a `JTable` (the view) rather than manipulating rows directly.

**Problem it solves:** Order and menu data need to be created, priced, persisted, displayed in a table, and exported to a receipt — all without duplicating the same data-shuffling logic for every different presentation.

**Why it fits:** `MenuItem`/`Order` hold no Swing dependencies, so the same objects can be serialized to disk, summarized into a receipt string, or rendered in a table without conversion code. `JTable`/`DefaultTableModel` is Swing's own Model-View split, so adding a row to the model automatically refreshes the view.

**Honest caveat:** This separation is partial, not textbook MVC. `MUFoodCornerAdvanced` currently plays the role of View, Controller, *and* persistence layer all at once (it builds the UI, handles events, and calls `saveData()`/`loadData()` directly). For a larger version of this app, extracting a dedicated controller/service class and a small persistence class would complete the separation and make the code easier to unit test.

---

## Known Limitations / Suggested Improvements

- **Persistence:** `.ser` files are brittle (any change to `MenuItem`/`Order`'s fields can break existing saved data) and store to the working directory rather than a fixed user-data location. A lightweight format like JSON or SQLite would be more robust.
- **Admin authentication:** The admin password is a hardcoded plaintext string in source. Fine for a class project/demo, but not suitable if this were ever deployed for real use.
- **Single God-class:** All UI, event handling, and persistence logic live in one class (`MUFoodCornerAdvanced`). Splitting it into `OrderService`, `MenuRepository`, and separate panel classes would make the Model-View separation above a true MVC and simplify unit testing.
- **Test coverage:** Only `MenuItem`'s constructor/subtotal logic is currently tested. `Order` total/discount calculation and `MUFoodCornerAdvanced`'s discount rules (`getDiscount()`) have no automated tests yet.

