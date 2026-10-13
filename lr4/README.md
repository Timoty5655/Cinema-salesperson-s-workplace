# Cinema-salesperson-s-workplace

Предметна область: **робоче місце касира кінотеатру**.

## Лабораторна робота №4 — інтерфейси та абстрактні класи

### Сутність і поведінка, що змінюється
Касир оформлює замовлення на квитки. Різними бувають:
1. **спосіб розрахунку знижки** — патерн **Strategy**;
2. **спосіб оплати** — патерн **Template Method**.

### Структура
| Файл | Роль |
|------|------|
| `DiscountStrategy` | інтерфейс знижки; default-методи `describe()`, `discountAmount()` |
| `NoDiscount`, `PercentDiscount`, `FixedDiscount`, `GroupDiscount` | реалізації стратегій (`FixedDiscount` і `GroupDiscount` перевизначають `describe()`) |
| `PaymentMethod` | інтерфейс оплати; default-метод `feeInfo()` |
| `AbstractPayment` | абстрактний клас: `final process()` — шаблонний метод, абстрактний `doPay()` |
| `CashPayment`, `CardPayment`, `OnlinePayment` | способи оплати |
| `Refundable` | окремий інтерфейс повернення коштів (default `refundPolicy()`) |
| `InvalidTicketDataException` | власний виняток з ЛР3 |
| `lr4cinema.java` | демонстрація поліморфізму |

### Коли абстрактний клас, а коли інтерфейс
- **Інтерфейс** (`DiscountStrategy`, `Refundable`) — коли потрібен лише контракт, а реалізації не мають спільного стану. Клас може реалізувати кілька інтерфейсів.
- **Абстрактний клас** (`AbstractPayment`) — коли є спільний стан (`name`) і спільний алгоритм (`process`), який не варто дублювати. Клас може успадкувати лише один абстрактний клас.
- `Refundable` — окремий інтерфейс, бо готівка не підтримує повернення; `CardPayment` та `OnlinePayment` реалізують одразу два контракти.

### Запуск (Java 25)
```bash
cd lr4
java lr4cinema.java
```
Усі `.java` файли мають лежати в одній папці.
