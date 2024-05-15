package org.yasin.federationofconsciouscitizensmindmapgraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.widget.Toast
import androidx.navigation.Navigation
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.min

//import com.almeros.android.multitouch.RotateGestureDetector
//import com.almeros.android.multitouch.MoveGestureDetector
//import com.almeros.android.multitouch.ShoveGestureDetector


class MyCustomView(context: Context, attrs: AttributeSet) :
    View(context, attrs) {
    private val circles = mutableListOf<Circle>()
    private var selectedCircle: Circle? = null
    private val paint = Paint()

    private var mScaleFactor = 1f
    private val mPaint: Paint = Paint()
    // private val mScaleDetector: ScaleGestureDetector


    //private var mPosX = 0f
    //private var mPosY = 0f
    private var mLastTouchX = 0f
    private var mLastTouchY = 0f

    private var mLastTouchX1 = 0f
    private var mLastTouchY1 = 0f
    private var mLastTouchX2 = 0f
    private var mLastTouchY2 = 0f

    private var degree = 0f

    private var screenWidth = 0f
    private var screenHeight = 0f
    private var isInitialized = false

    private val mImageHeight = height
    private var mImageWidth = width

    var scaledImageCenterX = (mImageWidth * mScaleFactor) / 2
    var scaledImageCenterY = (mImageHeight * mScaleFactor) / 2

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Сохраняем размеры экрана
        screenWidth = w.toFloat()
        screenHeight = h.toFloat()



        // Инициализируем круги здесь, когда размеры экрана уже известны
        if (!isInitialized) {
            initCircles()
            isInitialized = true
        }
    }

//    private fun initCircles() {
//        // Добавляем несколько кругов
//        log(context, "Добавляем несколько кругов ")
//
//        // Первый круг ("Парламент") посередине сверху
//        circles.add(Circle(screenWidth / 2f, screenHeight / 4f, 50f, Color.DKGRAY, "Парламент"))
//
//        // Второй круг ("Правительство") ниже и левее
//        circles.add(Circle(screenWidth / 3f, screenHeight / 2f, 75f, Color.DKGRAY, "Правительство"))
//
//        // Третий круг ("Судебная система") ниже и правее
//        circles.add(Circle(screenWidth * 2 / 3f, screenHeight * 3 / 4f, 100f, Color.DKGRAY, "Судебная система"))
//    }

    //var citizens: List<Citizen> = listOf()

    private fun getCitizens(): List<Citizen> {
        MyApp.log("getCitizens()")
        val citizenPreferences = CitizenPreferences(context)
        return citizenPreferences.getCitizens()
    }


    private fun initCircles() {
        // Добавляем несколько кругов
        log(context, "Добавляем несколько кругов ")

        // Устанавливаем размер текста
        paint.textSize = 40f

        // Первый круг ("Парламент") посередине сверху
        val parliamentRadius = paint.measureText("Парламент") / 2 + 10
        circles.add(Circle(screenWidth / 2f, screenHeight / 4f, parliamentRadius, Color.DKGRAY, "Парламент"))

        // Второй круг ("Правительство") ниже и левее
        val governmentRadius = paint.measureText("Правительство") / 2 + 10
        circles.add(Circle(screenWidth / 4f, screenHeight / 2f, governmentRadius, Color.DKGRAY, "Правительство"))

        // Третий круг ("Судебная система") ниже и правее
        val judicialSystemRadius = paint.measureText("Судебная система") / 2 + 10
        circles.add(Circle(screenWidth * 3 / 4f, screenHeight / 2f, judicialSystemRadius, Color.DKGRAY, "Судебная система"))

        // Получаем список граждан
        val citizens = getCitizens()

        // Проверяем, не равен ли список граждан null и не пуст ли он
        if (citizens.isNotEmpty()) {
            // Добавляем круг для каждого гражданина
            val maxCirclesPerRow = screenWidth / (paint.textSize.toInt() * 2)
            val startHeight = screenHeight / 2f + 2 * maxOf(parliamentRadius, governmentRadius, judicialSystemRadius)
            var currentRow = 0
            var citizensInCurrentRow = 0
            for ((index, citizen) in citizens.withIndex()) {
                if ((index % maxCirclesPerRow).toInt() == 0) {
                    // Начинаем новый ряд
                    currentRow++
                    citizensInCurrentRow = min(maxCirclesPerRow.toInt(), (citizens.size - index).toInt())

                }
                val shortName = if (citizen.name.length > 4) citizen.name.substring(0, 4) else citizen.name
                val citizenRadius = paint.measureText(shortName) / 2 + 10
                val x = (index % citizensInCurrentRow + 0.5f) * screenWidth / citizensInCurrentRow
                val y = startHeight + (currentRow - 1) * 2 * citizenRadius
                circles.add(Circle(x, y, citizenRadius, Color.DKGRAY, Name = shortName))
            }
        } else {
            log(context, "Список граждан пуст")
        }
    }



    private val gestureDetector1 =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val adjustedX = (e.x - mFocusX) / mScaleFactor
                val adjustedY = (e.y - mFocusY) / mScaleFactor

                // Создаем массив координат для точки касания
                val touchPoint = floatArrayOf(e.x, e.y)

                // Создаем обратную матрицу
                val invertedMatrix = Matrix()
                mMatrix.invert(invertedMatrix)

                // Применяем обратную матрицу к точке касания
                invertedMatrix.mapPoints(touchPoint)


// Обработка двойного клика внутри окружности
                for (circle in circles) {


                    if (circle.isInside(touchPoint[0], touchPoint[1])) {
                        // Действия, если точка находится внутри окружности

                        log(context, "Двойной клик детектед ")


                        selectedCircle = null
                        circle.color = Color.GREEN // меняем цвет при нажатии
                        invalidate()

                        //val navController = Navigation.findNavController(this@MyCustomView)
                        //navController.navigate(R.id.action_FirstFragment_to_SecondFragment)

                        return true
                    }
                }

                return super.onDoubleTap(e)
            }
        })

    init {
        //mPaint.color = 0xFF000000.toInt()
        mPaint.color = Color.DKGRAY
        //mScaleDetector = ScaleGestureDetector(context, ScaleListener())
    }

    private inner class ScaleListener1 : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        var mStartPosX = 0f
        var mStartPosY = 0f
        var mStartTouchX = 0f
        var mStartTouchY = 0f
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            // Запоминаем начальные координаты касания
            //mStartTouchX = detector.focusX
            //mStartTouchY = detector.focusY
            //  mStartPosX = mPosX
            //  mStartPosY = mPosX
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor

            // Не даем виджету стать слишком большим или слишком маленьким
            // mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor * scaleFactor, 5.0f))

            // Обновляем смещение, чтобы масштабирование происходило относительно точки касания
            val focusX = detector.focusX
            val focusY = detector.focusY
            mFocusX += (focusX - mStartTouchX  ) * (1- mScaleFactor)
            mFocusY += (focusY- mStartTouchY ) * (1- mScaleFactor)
            //mPosX += (mLastTouchX - focusX) * (1 - scaleFactor) * mScaleFactor
            //mPosY += (mLastTouchY - focusY) * (1 - scaleFactor) * mScaleFactor
            // mPosX = mStartPosX + focusX * (1- mScaleFactor)
            // mPosY = mStartPosY + focusY * (1 -mScaleFactor)


            mStartTouchX = focusX
            mStartTouchY = focusY

            invalidate()
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            // Здесь вы можете выполнить любую необходимую очистку или завершающие действия

            // Обновляем смещение, чтобы масштабирование происходило относительно точки касания
            //val focusX = detector.focusX
            //val focusY = detector.focusY
            //mPosX += (mLastTouchX - focusX) * mScaleFactor
            //mPosY += (mLastTouchY - focusY) * mScaleFactor
            //mPosX += (mLastTouchX - focusX) * (1 - scaleFactor) * mScaleFactor
            //mPosY += (mLastTouchY - focusY) * (1 - scaleFactor) * mScaleFactor
            //mPosX = mStartPosX + mStartTouchX * (1- mScaleFactor)
            //mPosY = mStartPosY + mStartTouchY * (1 -mScaleFactor)


            //mLastTouchX = focusX
            //mLastTouchY = focusY

            //invalidate()
            //return true
        }
    }


//    init {
//        // Добавляем несколько кругов
//        log(context, "Добавляем несколько кругов ")
//
//        // Первый круг ("Парламент") посередине сверху
//        circles.add(Circle(screenWidth / 2f, screenHeight / 4f, 50f, Color.DKGRAY, "Парламент"))
//
//        // Второй круг ("Правительство") ниже и левее
//        circles.add(Circle(screenWidth / 3f, screenHeight / 2f, 75f, Color.DKGRAY, "Правительство"))
//
//        // Третий круг ("Судебная система") ниже и правее
//        circles.add(Circle(screenWidth * 2 / 3f, screenHeight * 3 / 4f, 100f, Color.DKGRAY, "Судебная система"))
//    }


    override fun onDraw(canvas: Canvas) {
        log(context, "onDraw ")
        super.onDraw(canvas)

        canvas.save()

        // Применяем поворот к холсту
        //canvas.rotate(degree)
        //canvas.translate(mPosX, mPosY)

        //canvas.scale(mScaleFactor, mScaleFactor)
        //
        //canvas.translate(100f, 0f)
        scaledImageCenterX = (mImageWidth * mScaleFactor) / 2
        scaledImageCenterY = (mImageHeight * mScaleFactor) / 2


        mMatrix.reset()
        mMatrix.postScale(mScaleFactor, mScaleFactor, mFocusXScale, mFocusYScale)
        mMatrix.postTranslate(mFocusX , mFocusY )

        //mMatrix.postRotate(mRotationDegrees, scaledImageCenterX, scaledImageCenterY)


        canvas.concat(mMatrix)



        // Здесь вы можете рисовать что угодно, что должно быть масштабируемым.

        // Рисуем чёрную линию в верхней части CustomView
        paint.color = Color.GREEN
        paint.strokeWidth = 10f // Устанавливаем толщину линии в 10 пикселей
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, paint)


        // Рисуем чёрную линию в нижней части CustomView
        canvas.drawLine(0f, height.toFloat() - 1, width.toFloat() - 1, height.toFloat() - 1, paint)

        // Рисуем чёрную линию в левой части CustomView
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), paint)

        // Рисуем чёрную линию в правой части CustomView
        canvas.drawLine(width.toFloat() - 1, 0f, width.toFloat() - 1, height.toFloat() - 1, paint)

        // Рисуем круги
        for (circle in circles) {
            paint.color = circle.color
            canvas.drawCircle(circle.x, circle.y, circle.radius, paint)

//            // Добавляем надписи на кругах
//            paint.color = Color.WHITE // Выберите цвет для текста
//            paint.textSize = 30f // Выберите размер текста
//            canvas.drawText(circle.Name, circle.x, circle.y, paint)

            // Добавляем надписи внутри кругов
            paint.color = Color.WHITE // Выберите цвет для текста
            paint.textSize = 40f // Выберите размер текста
            paint.textAlign = Paint.Align.CENTER // Выравнивание текста по центру

            // Вычисляем координаты для текста, чтобы он оказался внутри круга
            val textX = circle.x
            val textY = circle.y - ((paint.descent() + paint.ascent()) / 2)

            canvas.drawText(circle.Name, textX, textY, paint)

        }

        canvas.restore()

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        log(context, "onTouchEvent ")
        // mScaleDetector.onTouchEvent(event)
        //gestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        if (event.pointerCount > 1) {

        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                log(context, "MotionEvent.ACTION_DOWN ")
                // Создаем массив координат для точки касания
                val touchPoint = floatArrayOf(event.x, event.y)

                // Создаем обратную матрицу
                val invertedMatrix = Matrix()
                mMatrix.invert(invertedMatrix)

                // Применяем обратную матрицу к точке касания
                invertedMatrix.mapPoints(touchPoint)


                for (circle in circles) {
                    /*  val adjustedX = (event.x - mFocusX) / mScaleFactor
                      val adjustedY = (event.y - mFocusY) / mScaleFactor

                      if (circle.isInside(adjustedX, adjustedY)) {
                          // Действия, если точка находится внутри окружности
                          //if (circle.color != Color.GREEN) {
                              selectedCircle = circle
                              circle.color = 0xFFFF0000.toInt() // меняем цвет при нажатии
                              invalidate()

                          return true
                      }*/

                    if (circle.isInside(touchPoint[0], touchPoint[1])) {
                        // Действия, если точка находится внутри окружности

                        //Toast.makeText(context, "select Circle", Toast.LENGTH_SHORT).show()
                        selectedCircle = circle
                        circle.color = 0xFFFF0000.toInt() // меняем цвет при нажатии
                        invalidate()

                        return true
                    }
                }
                /*   val x = event.x
                   val y = event.y

                   mLastTouchX = x
                   mLastTouchY = y*/
            }

            MotionEvent.ACTION_MOVE -> {
                log(context, "MotionEvent.ACTION_MOVE ")

                selectedCircle?.let {
                    // Создаем массив координат для точки касания
                    val touchPoint = floatArrayOf(event.x, event.y)

                    // Создаем обратную матрицу
                    val invertedMatrix = Matrix()
                    mMatrix.invert(invertedMatrix)

                    // Применяем обратную матрицу к точке касания
                    invertedMatrix.mapPoints(touchPoint)

                    it.x = touchPoint[0]
                    it.y = touchPoint[1]
                    invalidate()
                    return true
                } ?: run {
                    /*   val x = event.x
                       val y = event.y

                       // Вычисляем смещение
                       val dx = x - mLastTouchX
                       val dy = y - mLastTouchY

                       mFocusX += dx
                       mFocusY += dy

                       invalidate()

                       mLastTouchX = x
                       mLastTouchY = y*/
                }
//                mLastTouchX = event.x
//                mLastTouchY = event.y

            }


            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                log(context, "возвращаем первоначальный цвет ")
                if (selectedCircle?.baseColor == Color.GREEN)
                {
                    //Toast.makeText(context, "Color.GREEN", Toast.LENGTH_SHORT).show()
                    selectedCircle?.color = Color.GREEN // возвращаем первоначальный цвет
                    selectedCircle = null
                }
                else {
                    selectedCircle?.color = Color.DKGRAY // возвращаем первоначальный цвет
                    selectedCircle = null
                    //Toast.makeText(context, "возвращаем первоначальный цвет", Toast.LENGTH_SHORT)
                    //    .show()
                    invalidate()
                }
            }

        }

        mMoveDetector.onTouchEvent(event)
        //mShoveDetector.onTouchEvent(event)
        mScaleDetector.onTouchEvent(event)
        //mRotateDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        invalidate()
        // Передаем все события касания в ScaleGestureDetector
//        log(context,"Передаем все события касания в ScaleGestureDetector ")
//        mScaleDetector.onTouchEvent(event)
//        log(context,"Передали ")
        //return super.onTouchEvent(event)



        return true
    }

    // Создаем GestureDetector
    val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            // Здесь обрабатываем двойной клик
            //Toast.makeText(context, "Двойной клик", Toast.LENGTH_SHORT).show()
            val touchPoint = floatArrayOf(e.x, e.y)
            val invertedMatrix = Matrix()
            mMatrix.invert(invertedMatrix)
            invertedMatrix.mapPoints(touchPoint)

            for (circle in circles) {
                if (circle.isInside(touchPoint[0], touchPoint[1])) {
                    // Действия, если двойной клик был внутри окружности
                    if (circle.baseColor == Color.DKGRAY) {
                        selectedCircle = null
                        circle.color = Color.GREEN // меняем цвет при двойном клике
                        circle.baseColor = Color.GREEN
                        invalidate()
                        //Toast.makeText(context, "Двойной клик на окружности", Toast.LENGTH_SHORT)
                        //    .show()

                        val navController = Navigation.findNavController(this@MyCustomView)
                        //navController.navigate(R.id.action_FirstFragment_to_cardViewScrollingFragment)

                        val action = FirstFragmentDirections.actionFirstFragmentToCardViewScrollingFragment(circle.Name)
                        navController.navigate(action)


                        return true
                    } else {
                        if (circle.baseColor == Color.GREEN) {
                            circle.baseColor = Color.DKGRAY
                            circle.color = Color.DKGRAY
                            invalidate()
                            //Toast.makeText(context, "Двойной клик на окружности вернули цвет", Toast.LENGTH_SHORT).show()
                            return true
                        }
                    }
                }
            }
            return super.onDoubleTap(e)
        }
    })


    data class Circle(
        var x: Float,
        var y: Float,
        val radius: Float,
        var color: Int = Color.DKGRAY,
        var Name: String,
        var baseColor: Int = Color.DKGRAY
    ) {
        fun isInside(px: Float, py: Float): Boolean {
            val dx = px - x
            val dy = py - y
            return dx * dx + dy * dy <= radius * radius
        }
    }

    private fun log1(context: Context, message: String) {

        Log.d("Example", message) // Вывод в Logcat
        println(message) // Вывод в стандартную консоль

        // Запись в файл лога
        try {
            val fos = context.openFileOutput("log.txt", Context.MODE_APPEND)
            fos.write("$message\n".toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
//             val activity = context as Activity
//            val handler = Handler(Looper.getMainLooper())
//
//            handler.post {
//                val textView = activity.findViewById<TextView>(R.id.textview_second)
//                textView.append("\n$message")
//            }

        } catch (e: Exception) {
            TODO("Not yet implemented")
            Log.d("Example", e.message.toString())
            e.printStackTrace()
        }

    }

    // Функция для логирования
    private fun log(context: Context, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentDateTime =
                LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            val dateTimeString = currentDateTime.format(formatter)

            val message_new = dateTimeString + " " + message
            println("Текущая дата и время: $dateTimeString")
            Log.d("Example", message_new) // Вывод в Logcat
            println(message_new) // Вывод в стандартную консоль

            // Запись в файл лога
            try {
                val fos = context.openFileOutput("log.txt", Context.MODE_APPEND)
                fos.write("$message_new\n".toByteArray())
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    /* private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
         override fun onScale(detector: ScaleGestureDetector): Boolean {
             mScaleFactor *= detector.scaleFactor

             // Не даем виджету стать слишком большим или слишком маленьким
             mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f))




             invalidate()
             return true
         }

         override fun onScaleEnd(detector: ScaleGestureDetector) {
             mLastTouchX = detector.focusX
             mLastTouchY = detector.focusY
         }
     }*/
    //var mScaleFactor1: Float = .4f
    private val mMatrix = Matrix()
    // private var mScaleFactor = .4f
    private var mRotationDegrees = 0f
    private var mFocusX = 0f
    private var mFocusY = 0f
    private var mFocusXScale = 0f
    private var mFocusYScale = 0f
    private var mAlpha = 255


    //val mScaleDetector1: ScaleGestureDetector? = null
    //val mRotateDetector: RotateGestureDetector? = null
    //private val mMoveDetector: MoveGestureDetector? = null
    //private val mShoveDetector: ShoveGestureDetector? = null

    // Setup Gesture Detectors

    // Создайте экземпляры детекторов
    val mRotateGestureDetector = RotateGestureDetector(context, RotateListener())
    val mMoveGestureDetector = MoveGestureDetector(context, MoveListener())
    val mShoveGestureDetector = ShoveGestureDetector(context, ShoveListener())

    // Setup Gesture Detectors
    val mScaleDetector = ScaleGestureDetector(context, ScaleListener())
    val mRotateDetector = RotateGestureDetector(context, RotateListener())
    val mMoveDetector = MoveGestureDetector(context, MoveListener())
    val mShoveDetector = ShoveGestureDetector(context, ShoveListener())

    private inner class ScaleListener : SimpleOnScaleGestureListener() {

        var mStartPosX = 0f
        var mStartPosY = 0f
        var mStartTouchX = 0f
        var mStartTouchY = 0f
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            // Запоминаем начальные координаты касания
            mStartTouchX = detector.focusX
            mStartTouchY = detector.focusY
            //  mStartPosX = mPosX
            //  mStartPosY = mPosX
            return true
        }


        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.getScaleFactor() // scale change since previous event

            // Don't let the object get too small or too large.
            mScaleFactor = max(0.1, min(mScaleFactor.toDouble(), 10.0)).toFloat()

            // mFocusX+=detector.focusX
            // mFocusY+=detector.focusY

            // Обновляем смещение, чтобы масштабирование происходило относительно точки касания
            val focusX = detector.focusX
            val focusY = detector.focusY
            mFocusXScale = focusX
            mFocusYScale = focusY
            //mPosX += (mLastTouchX - focusX) * (1 - scaleFactor) * mScaleFactor
            //mPosY += (mLastTouchY - focusY) * (1 - scaleFactor) * mScaleFactor
            // mPosX = mStartPosX + focusX * (1- mScaleFactor)
            // mPosY = mStartPosY + focusY * (1 -mScaleFactor)


            mStartTouchX = focusX
            mStartTouchY = focusY

            invalidate()

            return true
        }
    }


    private inner class RotateListener : RotateGestureDetector.SimpleOnRotateGestureListener() {
        override fun onRotate(detector: RotateGestureDetector): Boolean {
            mRotationDegrees -= detector.getRotationDegreesDelta()
            return true
        }
    }


    private inner class MoveListener : MoveGestureDetector.SimpleOnMoveGestureListener() {
        override fun onMove(detector: MoveGestureDetector): Boolean {
            val d = detector.focusDelta
            mFocusX += d.x
            mFocusY += d.y

            // mFocusX = detector.getFocusX();
            // mFocusY = detector.getFocusY();
            return true
        }
    }


    private inner class ShoveListener : ShoveGestureDetector.SimpleOnShoveGestureListener() {
        override fun onShove(detector: ShoveGestureDetector): Boolean {
            mAlpha += (detector.shovePixelsDelta).toInt()
            if (mAlpha > 255) mAlpha = 255 else if (mAlpha < 0) mAlpha = 0
            return true
        }
    }


}
