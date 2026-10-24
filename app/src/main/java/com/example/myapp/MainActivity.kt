package com.example.myapp

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

data class Item(val big: String, val name: String, val say: String = name, val tag: String = "")
data class Lesson(val title: String, val color: Int, val items: List<Item>)

class MainActivity : Activity() {
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var goBack: (() -> Unit)? = null

    private val cream = Color.parseColor("#FFF8E1")
    private val orange = Color.parseColor("#E65100")
    private val dark = Color.parseColor("#333333")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(0.8f)
                ttsReady = true
            }
        }
        home()
    }

    override fun onDestroy() {
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onBackPressed() {
        val g = goBack
        if (g != null) g() else super.onBackPressed()
    }

    // ---------- helpers ----------
    private fun speak(t: String) {
        if (ttsReady) tts?.speak(t, TextToSpeech.QUEUE_FLUSH, null, "x")
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun light(c: Int) = Color.rgb(
        255 - (255 - Color.red(c)) / 6,
        255 - (255 - Color.green(c)) / 6,
        255 - (255 - Color.blue(c)) / 6
    )

    private fun btn(label: String, c: Int, onClick: () -> Unit): Button {
        val b = Button(this)
        b.text = label
        b.textSize = 20f
        b.isAllCaps = false
        b.setTextColor(Color.WHITE)
        b.typeface = Typeface.DEFAULT_BOLD
        val bgd = GradientDrawable()
        bgd.setColor(c)
        bgd.cornerRadius = dp(24).toFloat()
        b.background = bgd
        b.setPadding(dp(16), dp(14), dp(16), dp(14))
        b.setOnClickListener { onClick() }
        return b
    }

    private fun tv(label: String, size: Float, color: Int = dark): TextView {
        val t = TextView(this)
        t.text = label
        t.textSize = size
        t.setTextColor(color)
        t.gravity = Gravity.CENTER
        t.typeface = Typeface.DEFAULT_BOLD
        return t
    }

    private fun lp(top: Int = 0) = LinearLayout.LayoutParams(-1, -2).apply {
        setMargins(dp(24), dp(top), dp(24), 0)
    }

    private fun rowLp() = LinearLayout.LayoutParams(0, -2, 1f).apply {
        setMargins(dp(4), 0, dp(4), 0)
    }

    private fun bigSize(s: String) = when {
        s.length <= 4 -> 110f
        s.length <= 20 -> 56f
        else -> 44f
    }

    private fun screen(bg: Int, back: (() -> Unit)?, build: LinearLayout.() -> Unit) {
        goBack = back
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, dp(24), 0, dp(24))
            build()
        }
        val sv = ScrollView(this)
        sv.setBackgroundColor(bg)
        sv.isFillViewport = true
        sv.addView(col)
        setContentView(sv)
    }

    // ---------- lesson data ----------
    private val numWords = listOf(
        "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
        "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen",
        "Eighteen", "Nineteen", "Twenty"
    )
    private val objs = listOf("🍎", "⭐", "🐟", "🎈")

    private fun nums(to: Int) = (1..to).map {
        Item(objs[it % 4].repeat(it), "$it - ${numWords[it - 1]}", numWords[it - 1])
    }

    private fun alphabet(): List<Item> {
        val a = listOf(
            "🍎" to "Apple", "⚽" to "Ball", "🐱" to "Cat", "🐶" to "Dog", "🐘" to "Elephant",
            "🐟" to "Fish", "🍇" to "Grapes", "🎩" to "Hat", "🍦" to "Ice cream", "🃏" to "Joker",
            "🦘" to "Kangaroo", "🦁" to "Lion", "🐵" to "Monkey", "👃" to "Nose", "🍊" to "Orange",
            "🦜" to "Parrot", "👸" to "Queen", "🐰" to "Rabbit", "☀️" to "Sun", "🐯" to "Tiger",
            "☂️" to "Umbrella", "🚐" to "Van", "⌚" to "Watch", "🎄" to "Xmas tree",
            "⛵" to "Yacht", "🦓" to "Zebra"
        )
        return a.mapIndexed { i, p ->
            val l = 'A' + i
            Item(p.first, "$l for ${p.second}", "$l for ${p.second}", "$l ${l.lowercaseChar()}")
        }
    }

    private fun words(): List<Item> {
        val w = listOf(
            "🐱" to "CAT", "🐶" to "DOG", "☀️" to "SUN", "🚌" to "BUS", "🐷" to "PIG",
            "🎩" to "HAT", "🐞" to "BUG", "🥚" to "EGG", "🐮" to "COW", "🐝" to "BEE"
        )
        return w.map { Item(it.first, it.second, it.second.toList().joinToString(", ") + ". " + it.second.lowercase()) }
    }

    private fun lessonList(lv: String): List<Lesson> {
        val colors = Lesson("🎨 Colors", Color.parseColor("#E53935"), listOf(
            Item("❤️", "Red"), Item("💛", "Yellow"), Item("💙", "Blue"),
            Item("💚", "Green"), Item("🧡", "Orange"), Item("💜", "Purple")))
        val shapes = Lesson("🔷 Shapes", Color.parseColor("#8E24AA"), listOf(
            Item("⭕", "Circle"), Item("⬜", "Square"), Item("🔺", "Triangle"),
            Item("⭐", "Star"), Item("❤️", "Heart"), Item("🔷", "Diamond")))
        val animals = Lesson("🐶 Animals", Color.parseColor("#FB8C00"), listOf(
            Item("🐶", "Dog"), Item("🐱", "Cat"), Item("🐮", "Cow"), Item("🐘", "Elephant"),
            Item("🦁", "Lion"), Item("🐵", "Monkey"), Item("🐟", "Fish"), Item("🐦", "Bird")))
        val fruits = Lesson("🍎 Fruits", Color.parseColor("#D81B60"), listOf(
            Item("🍎", "Apple"), Item("🍌", "Banana"), Item("🍇", "Grapes"),
            Item("🍊", "Orange"), Item("🍓", "Strawberry"), Item("🍉", "Watermelon")))
        val abc = Lesson("🔤 Alphabets", Color.parseColor("#43A047"), alphabet())
        val w = Lesson("✏️ Three-letter words", Color.parseColor("#00897B"), words())
        fun n(to: Int) = Lesson("🔢 Numbers 1-$to", Color.parseColor("#1E88E5"), nums(to))
        return when (lv) {
            "Nursery" -> listOf(colors, shapes, animals, n(5))
            "LKG" -> listOf(abc, n(10), fruits, animals)
            else -> listOf(abc, n(20), w, animals)
        }
    }

    // ---------- screens ----------
    private fun home() {
        screen(cream, null) {
            addView(tv("🎒", 64f), lp())
            addView(tv("Little Learners", 32f, orange), lp())
            addView(tv("Choose your class", 18f), lp(4))
            addView(btn("🌱  Nursery", Color.parseColor("#43A047")) { levelScreen("Nursery") }, lp(24))
            addView(btn("🌼  LKG", Color.parseColor("#1E88E5")) { levelScreen("LKG") }, lp(16))
            addView(btn("🎓  UKG", Color.parseColor("#8E24AA")) { levelScreen("UKG") }, lp(16))
        }
        speak("Welcome! Choose your class.")
    }

    private fun levelScreen(lv: String) {
        val ls = lessonList(lv)
        screen(cream, { home() }) {
            addView(tv(lv, 32f, orange), lp())
            addView(tv("Pick a lesson", 18f), lp(4))
            ls.forEach { l ->
                addView(btn(l.title, l.color) { learn(lv, l, 0) }, lp(16))
            }
            addView(btn("🏠 Home", Color.GRAY) { home() }, lp(24))
        }
    }

    private fun learn(lv: String, l: Lesson, i: Int) {
        val item = l.items[i]
        screen(light(l.color), { levelScreen(lv) }) {
            addView(tv(l.title, 22f, l.color), lp())
            addView(tv("${i + 1} / ${l.items.size}", 14f), lp(4))
            if (item.tag.isNotEmpty()) addView(tv(item.tag, 56f, l.color), lp(12))
            addView(tv(item.big, bigSize(item.big)), lp(8))
            addView(tv(item.name, 32f), lp(8))
            val row = LinearLayout(this@MainActivity)
            row.orientation = LinearLayout.HORIZONTAL
            row.gravity = Gravity.CENTER
            row.addView(btn("◀", l.color) { if (i > 0) learn(lv, l, i - 1) }, rowLp())
            row.addView(btn("🔊", l.color) { speak(item.say) }, rowLp())
            row.addView(btn("▶", l.color) {
                if (i < l.items.size - 1) learn(lv, l, i + 1)
                else quiz(lv, l, 0, 0, l.items.shuffled().take(5))
            }, rowLp())
            addView(row, lp(24))
            addView(btn("🎯 Quiz time", Color.parseColor("#F57C00")) {
                quiz(lv, l, 0, 0, l.items.shuffled().take(5))
            }, lp(16))
            addView(btn("📚 Lessons", Color.GRAY) { levelScreen(lv) }, lp(12))
        }
        speak(item.say)
    }

    private fun quiz(lv: String, l: Lesson, n: Int, score: Int, qs: List<Item>) {
        if (n >= qs.size) {
            result(lv, l, score, qs.size)
            return
        }
        val q = qs[n]
        val opts = (l.items.filter { it != q }.shuffled().take(2) + q).shuffled()
        screen(light(l.color), { learn(lv, l, 0) }) {
            var tried = false
            addView(tv("Question ${n + 1} of ${qs.size}", 16f), lp())
            addView(tv("What is this?", 24f, l.color), lp(8))
            addView(tv(q.big, bigSize(q.big)), lp(8))
            opts.forEach { o ->
                addView(btn(o.name, l.color) {
                    if (o == q) {
                        speak("Great job!")
                        quiz(lv, l, n + 1, score + (if (tried) 0 else 1), qs)
                    } else {
                        tried = true
                        speak("Try again")
                        Toast.makeText(this@MainActivity, "Try again 🙂", Toast.LENGTH_SHORT).show()
                    }
                }, lp(12))
            }
        }
        speak("What is this?")
    }

    private fun result(lv: String, l: Lesson, score: Int, total: Int) {
        screen(light(l.color), { levelScreen(lv) }) {
            addView(tv("🎉", 80f), lp(24))
            addView(tv("You got $score out of $total!", 26f, l.color), lp(8))
            addView(tv("⭐".repeat(score), 40f), lp(8))
            addView(btn("🔁 Play again", l.color) {
                quiz(lv, l, 0, 0, l.items.shuffled().take(5))
            }, lp(24))
            addView(btn("📚 Back to lessons", Color.GRAY) { levelScreen(lv) }, lp(12))
        }
        speak(if (score >= total - 1) "Well done!" else "Good try!")
    }
}
