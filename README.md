# 📋 Telegram Queue Bot

## 🤖 Purpose

A lightweight Telegram bot for **simple queue management in group chats**, e.g., for lab submissions, presentations, or turn-taking.

- **Anyone can use it to create, manage, or join queues.**
- Supports up to **5 queues per chat**.

---

## ⚙️ How it works

- During auto-registration, users simply **send a number to claim a position**.
- If something is wrong (e.g., position taken), the bot will notify immediately.
- The bot operates precisely to the **second based on Kyiv time**, holding sent messages in a separate frozen thread to enable exact millisecond-based registration starts.
- Works like a `queue` structure: **first in, first out**.

---

## 🛠 Environment Setup

You need a `.env` file with:

DB_URL=<your_database_url>,
DB_USER=<your_database_user>,
DB_PASSWORD=<your_database_password>,
MAX_QUEUE_COUNT=<max_count>

---

## 🛡 Admin Commands

- `create "queue_name" "start_time to end_time date"`  
  Create a queue.  
  _Example:_ `create Lab 10:00 to 12:00 17.06.2025`

- `delete "queue_name"`  
  Delete a queue.

- `settime "queue_name" "start_time" to "end_time" "date"`  
  Update auto-registration timing for a queue.

- `rebuild "queue_name"`  
  Rebuild the queue, keeping only those who haven't completed.

- `remove "queue_name" @username`  
  Remove a user from a queue, automatically shifting others up.

- `insert "queue_name" @username pos "position"`  
  Pre-register a user at a specific position.

---

## 👥 User Commands

- `list`  
  View all queues in the chat.

- `info "queue_name"`  
  View details of a queue.

- `join "queue_name" "desired_position"`  
  Join a queue at a desired position.

- `leave "queue_name"`  
  Leave a queue.

- `swap "queue_name" "position_number"`  
  Swap your place with another position if allowed.

- `complete "queue_name"`  
  Mark your task as completed.

- `retake "queue_name"`  
  Mark yourself as retaking.

---

## 🛡 Admin Management

- `admins`  
  Show the list of admins.

- `admin reg @username`  
  Register a new admin. If there are no admins yet, `admin reg` alone makes you the main admin.

- `admin del @username`  
  Remove an admin. If the main admin removes themselves, a random admin becomes the new main admin.

- `admin raise @username`  
  Promote an admin to the main admin role.

---

## ℹ️ Features

✅ Pre-registration for users by admins.  
✅ Automatic position shifting when someone leaves the queue.  
✅ Auto-registration via simply sending a number in chat.  
✅ Validates that queues in the same chat do not overlap in time and date.

---

Feel free to **fork and adapt this bot** to your workflows or group needs.
