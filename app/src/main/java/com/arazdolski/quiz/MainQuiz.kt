package com.arazdolski.quiz

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import java.util.Collections.shuffle

class MainQuiz : AppCompatActivity() {

    private var txtQuestion: TextView? = null
    private var txtScore: TextView? = null
    private var txtQuestionCount: TextView? = null
    private var radioGroup: RadioGroup? = null
    private var r1: RadioButton? = null
    private var r2: RadioButton? = null
    private var r3: RadioButton? = null
    private var mSubmit: Button? = null
    private var mShowRightAns: Button? = null

    private var colorStateList: ColorStateList? = null

    private var questionSetsList: List<Question>? = null

    private var qCounter: Int = 0
    private var currQuestion: Question? = null
    private var qCountTotal: Int = 0

    private var score: Int = 0
    private var ans: Boolean = false

    private var onBackPressedTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_quiz)

        txtQuestion = findViewById(R.id.Question)
        txtScore = findViewById(R.id.Score)
        txtQuestionCount = findViewById(R.id.questionCount)
        radioGroup = findViewById(R.id.radioGroup)
        r1 = findViewById(R.id.radioButton1)
        r2 = findViewById(R.id.radioButton2)
        r3 = findViewById(R.id.radioButton3)
        mSubmit = findViewById(R.id.submitButton)
        mShowRightAns = findViewById(R.id.showRightAns)


        colorStateList = r1!!.textColors

        val questionDb = DatabaseHelper(this)
        questionSetsList = questionDb.questionSet

        qCountTotal = questionSetsList!!.size

        shuffle(questionSetsList!!)

        showQuestion()

        mSubmit!!.setOnClickListener {
            if (!ans) {

                if (r1!!.isChecked || r2!!.isChecked || r3!!.isChecked) {
                    check()
                } else {
                    showQuestion()
                }
            } else {
                showQuestion()
            }
            enableRadio()
        }

        mShowRightAns!!.setOnClickListener {

            radioGroup!!.clearCheck()
            showRightAns()
            disableRadio()
        }

    }


    private fun showQuestion() {

        r1!!.setTextColor(colorStateList)
        r2!!.setTextColor(colorStateList)
        r3!!.setTextColor(colorStateList)

        radioGroup!!.clearCheck()


        if (qCounter < qCountTotal) {
            currQuestion = questionSetsList!![qCounter]
            txtQuestion!!.text = currQuestion!!.getmQuestion()

            r1!!.text = currQuestion!!.getmOption1()
            r2!!.text = currQuestion!!.getmOption2()
            r3!!.text = currQuestion!!.getmOption3()

            qCounter++

            txtQuestionCount!!.text = "Question: $qCounter / $qCountTotal"

            ans = false

            mSubmit!!.text = getString(R.string.confirm)
        } else {
            finishQuizActivity()
        }

    }


    private fun check() {
        ans = true

        val radioSelected = findViewById<View>(radioGroup!!.checkedRadioButtonId) as RadioButton
        val answer = radioGroup!!.indexOfChild(radioSelected) + 1

        if (answer == currQuestion!!.getmRightAns()) {
            score += 10
            txtScore!!.text = "Score: $score"
        } else {
            score += 5
            txtScore!!.text = "Score: $score"
        }

        showRightAns()

    }

    private fun showRightAns() {

        r1!!.setTextColor(Color.RED)
        r2!!.setTextColor(Color.RED)
        r3!!.setTextColor(Color.RED)

        when (currQuestion!!.getmRightAns()) {
            1 -> {
                r1!!.setTextColor(Color.GREEN)
                txtQuestion!!.text = getString(R.string.Answer1correct)
            }
            2 -> {
                r2!!.setTextColor(Color.GREEN)
                txtQuestion!!.text = getString(R.string.Answer2correct)
            }
            3 -> {
                r3!!.setTextColor(Color.GREEN)
                txtQuestion!!.text = getString(R.string.Answer3correct)
            }
        }



        if (qCounter < qCountTotal) {
            mSubmit!!.text = getString(R.string.next)
        } else {
            mSubmit!!.text = getString(R.string.finish)
        }
    }

    private fun enableRadio() {
        r1!!.isEnabled = true
        r2!!.isEnabled = true
        r3!!.isEnabled = true
    }

    private fun disableRadio() {
        r1!!.isEnabled = false
        r2!!.isEnabled = false
        r3!!.isEnabled = false
    }


    private fun finishQuizActivity() {
        val rIntent = Intent()
        rIntent.putExtra(FINAL_SCORE, score)
        setResult(Activity.RESULT_OK, rIntent)
        finish()
    }

    override fun onBackPressed() {

        if (onBackPressedTime + 2000 > System.currentTimeMillis()) {
            finishQuizActivity()
        } else {
            Toast.makeText(this@MainQuiz, "Press Back Again", Toast.LENGTH_SHORT).show()
        }
        onBackPressedTime = System.currentTimeMillis()

    }

    companion object {

        val FINAL_SCORE = "FinalScore"
    }
}
