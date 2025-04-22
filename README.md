## 📋 Part 1 – Habit List  
- **Habit.kt** holds your data.  
- **item_habit.xml** defines each row.  
- **HabitAdapter + DiffUtil** gives you efficient list updates.  
- **HabitListFragment** hooks it all up with `submitList(...)`.

---

## 📝 Part 2 – Add Habit (User Input)  
- **fragment_add_habit.xml**: `EditText` for name & goal, `TimePicker`, “Save” button.  
- **AddHabitFragment**: Read inputs, build `Habit` object, save to file/in‑memory, navigate back.

---

## 🔄 Part 3 – Lifecycle Logging  
- Override `onCreate()`, `onStart()`, `onResume()`, `onPause()`, `onStop()`, `onDestroy()` in **MainActivity**.  
- Use `Log.d(TAG, "onStart() called")` in each.

---

## 🚧 Part 4 – Navigation & Fragments  
- **nav_graph.xml**: 3 destinations (HabitList, AddHabit, Quote).  
- `<NavHostFragment>` in **activity_main.xml**.  
- In fragments call `findNavController().navigate(...)`.

---

## 💾 Part 5 – File I/O (Persistent Storage)  
- Use `context.openFileOutput("habits.txt", MODE_PRIVATE)` / `openFileInput(...)`.  
- Serialize/deserialize habit list (JSON or CSV).  
- Helper methods:  
  ```kotlin
  fun saveHabits(context: Context, list: List<Habit>) { … }
  fun loadHabits(context: Context): List<Habit> { … }
  ```

---

## 🌐 Part 6 – Retrofit & Coroutines  
- **RetrofitClient** singleton with:
  ```kotlin
  interface QuoteApi {
    @GET("quotes") suspend fun getQuotes(): List<Quote>
  }
  ```
- In **QuoteFragment**:
  ```kotlin
  viewLifecycleOwner.lifecycleScope.launch {
    val quotes = RetrofitClient.api.getQuotes()
    tvQuote.text = quotes.random().text
  }
  ```

---

## 🕰 Part 7 – WorkManager  
- Define `HabitWorker : CoroutineWorker`.  
- Schedule with:
  ```kotlin
  PeriodicWorkRequestBuilder<HabitWorker>(1, TimeUnit.DAYS)
    .build()
  ```
- In `doWork()`, post a notification via NotificationManager.

---

## 🔋 Part 8 – BroadcastReceiver (Battery Low)  
- Create `BatteryReceiver : BroadcastReceiver`.  
- In `onReceive()`, run:
  ```kotlin
  Toast.makeText(context, "Sync paused due to low battery", Toast.LENGTH_LONG).show()
  ```
- Register for `Intent.ACTION_BATTERY_LOW` in manifest or code.

---

## 🔔 Part 9 – Foreground Service (Habit Timer)  
- `HabitTimerService : Service`.  
- In `onStartCommand()`:
  ```kotlin
  startForeground(NOTIF_ID, buildNotification("Habit Timer…"))
  ```
- Provide basic start/stop actions if time permits.

---

## 📱 Part 10 – UI Buttons & Navigation  
- On Habit List screen add Buttons:
  - **Add Habit** → `action_habitList_to_addHabit`
  - **Load Defaults** → insert sample data + `adapter.submitList(...)`
  - **Show Quote** → `action_habitList_to_quote`
- On Quote screen add **Show New Quote** to re‑launch your coroutine.

---

## 💡 General Exam Tips  
- **Paste snippets**: you’ll earn points for code that simply compiles.  
- **Use `Log.d()`** in lifecycle methods for quick marks.  
- **Copy manifest entries** for Receivers/Services.  
- **Hard‑code sample data** to verify your RecyclerView.  
- **Double‑check imports** (`androidx.*`) after pasting.  
- **Keep a mental map** of file locations:
  - `model/` → data classes  
  - `adapter/` → RecyclerView adapters  
  - `ui/fragments/` → your Fragments  
  - `network/` → Retrofit setup  
  - `work/` → WorkManager  
  - `service/` → Services & BroadcastReceivers
 



Model Helper Fragmnent adapter
