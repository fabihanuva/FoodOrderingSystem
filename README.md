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

Project Overview

MU Food Corner is a Java-based desktop food ordering system developed using Java Swing. The application allows customers to browse a menu, add food items to their cart, apply discount codes, place orders, and generate receipts. It also includes an admin panel for managing menu items and viewing order history. The system uses file serialization to save menu and order data, ensuring information is retained between sessions. The project follows object-oriented programming principles and provides a simple, user-friendly interface for both customers and administrators.

## Project Structure

```
MUFoodCorner/
├── pom.xml
├── src/
│   ├── main/java/org/example/
│   │   ├── MUFoodCornerAdvanced.java   # entry point, Swing UI, event handling, persistence
│   │   ├── OrderValidator.java         # required-field / phone / cart-not-empty rules
│   │   ├── DiscountService.java        # promo-code discount contract
│   │   ├── DefaultDiscountService.java # MU50 / OFF10 promo rules (real implementation)
│   │   └── OrderService.java           # builds an Order from a cart + a DiscountService
│   └── test/java/org/example/
│       ├── MenuItemTest.java                 # MenuItem: construction, subtotal, equality
│       ├── OrderTest.java                    # Order: assertThrows/DoesNotThrow/Timeout
│       ├── CoreAssertionsTest.java           # assertTrue/False/Null/NotNull/ArrayEquals, BeforeAll/AfterAll
│       ├── ParameterizedTests.java           # ValueSource/CsvSource/MethodSource/CsvFileSource
│       ├── OrderValidatorTest.java           # validation rules, incl. phone boundary values
│       ├── DefaultDiscountServiceTest.java   # real discount rules (no mocking)
│       ├── OrderServiceMockTest.java         # OrderService with a mocked DiscountService
│       └── OrderServiceIntegrationTest.java  # OrderService with the real DiscountService
├── orders.ser                                 # generated at runtime
└── menu.ser                                   # generated at runtime
```

## Getting Started

**Prerequisites:** JDK matching (or newer than) the version set in `pom.xml`, and Maven.

```bash
# Compile and run the test suite
mvn test

# Compile and launch the application
mvn compile exec:java -Dexec.mainClass="org.example.MUFoodCornerAdvanced"
```

If the `exec` plugin isn't pre-configured, Maven will resolve it automatically on first run, or you can build manually:

```bash
javac -d out src/main/java/org/example/*.java
java -cp out org.example.MUFoodCornerAdvanced
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

**Honest caveat:** This separation is partial, not textbook MVC. `MUFoodCornerAdvanced` currently plays the role of View and Controller (it builds the UI and handles events), but still calls `saveData()`/`loadData()` directly — persistence has not yet been extracted into its own class. See Known Limitations below.

### 5. Strategy + Dependency Injection

**Where:** `DiscountService` (interface) / `DefaultDiscountService` (the real MU50/OFF10 rules), injected into `OrderService` through its constructor. `MUFoodCornerAdvanced` holds one `DiscountService`, one `OrderService`, and one `OrderValidator` as fields and delegates to them from `getDiscount()` and `confirmOrder()` instead of computing discount/validation logic inline.

**Problem it solves:** The original code had the promo-code rules and the delivery-detail validation hardcoded directly inside the GUI class, which meant they could only be exercised by clicking through the actual UI — there was no way to unit test "does `MU50` give TK 50 off" without a running `JFrame`.

**Why it fits:** `DiscountService` is a Strategy — `OrderService` doesn't know or care whether it's `DefaultDiscountService` or a different promo-rule implementation, it just calls `getDiscount(...)`. Passing that dependency in through the constructor (rather than `OrderService` constructing its own `DefaultDiscountService`) is textbook Dependency Injection, and it's what makes `OrderServiceMockTest` possible — a test can swap in a mock `DiscountService` and verify `OrderService`'s own logic in isolation, while `OrderServiceIntegrationTest` and `DefaultDiscountServiceTest` exercise the real implementation. `OrderValidator` follows the same idea for the required-field/phone/cart-not-empty rules.

---

## Known Limitations / Suggested Improvements

- **Persistence:** `.ser` files are brittle (any change to `MenuItem`/`Order`'s fields can break existing saved data) and store to the working directory rather than a fixed user-data location. `saveData()`/`loadData()` also still live directly on `MUFoodCornerAdvanced` rather than a dedicated persistence class, so they aren't independently unit tested yet — a natural next extraction, following the same pattern used for `OrderValidator`/`OrderService`.
- **Admin authentication:** The admin password is a hardcoded plaintext string in source. Fine for a class project/demo, but not suitable if this were ever deployed for real use.
- **God-class, partially addressed:** Discount calculation and order-placement validation have been extracted into `DiscountService`/`OrderService`/`OrderValidator` and are unit tested independently of the UI. `MUFoodCornerAdvanced` still owns all Swing layout/event-wiring and the persistence calls, so it isn't a full MVC split, but it's no longer doing everything.
- **Test coverage:** `MenuItem`, `Order`, `DiscountService` (both mocked and real), and `OrderValidator` (including boundary values on the phone rule) are covered. Still untested: `saveData()`/`loadData()` serialization round-trip, and the admin panel's add/update/delete menu logic (both still Swing-coupled).

