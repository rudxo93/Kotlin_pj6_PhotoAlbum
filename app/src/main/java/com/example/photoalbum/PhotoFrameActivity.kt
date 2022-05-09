package com.example.photoalbum

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.timer

class PhotoFrameActivity: AppCompatActivity() {

    private val photoList = mutableListOf<Uri>() // 변경 가능한 Uri리스트 전역으로 선언

    private val photoImageView: ImageView by lazy {
        findViewById<ImageView>(R.id.photoImageView)
    }

    private val backgroundPhotoImageView: ImageView by lazy {
        findViewById<ImageView>(R.id.backgroundPhotoImageView)
    }

    private var currentPosition = 0

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoframe)

        getPhotoUriFromIntent()
    }

    private fun getPhotoUriFromIntent() {
        // 인텐트에 들어있는 데이터 가져오기
        val size = intent.getIntExtra("photoListSize", 0)
        for (i in 0..size) {
            // 사진 uri사이즈로부터 for문을 돌려 각각 photo[인덱스]로 지정했던 stringExtra를 가져온다.
            intent.getStringExtra("photo$i")?.let {
                // 이때 photolist에 넣어주기전에 Uri로 파싱하는것을 잊으면 안된다.
                photoList.add(Uri.parse(it))
            }
        }
    }

    private fun startTimer() {
        // 5초에 한 번씩 전환
        // 앱을 종료하고 난 뒤에도 timer가 실행되면 문제가 될 수 있음
        // onStop 시 종료 후 onStart 시 다시 키는 등 처리 필요
        timer = timer(period = 5 * 1000) {
            // timer는 메인 스레드가 아님
            runOnUiThread { // 메인 스레드에서 사용, 람다 호출
                Log.d("PhotoFrame", "startTimer 5초 지남")

                // 현재 가지고 있는 이미지 리스트 사이즈만큼 반복되면서 current, next로 증가시키며 이미지를 보여준다.
                val current = currentPosition
                val next = if (photoList.size <= currentPosition + 1) 0 else currentPosition + 1

                //  현재 이미지를 보여주고
                backgroundPhotoImageView.setImageURI(photoList[current])

                // 투명도 설정
                photoImageView.alpha = 0f
                photoImageView.setImageURI(photoList[next])
                // 애니메이션으로 다음 이미지를 1초간 전환해 보여줌
                photoImageView.animate()
                    .alpha(1.0f) // 알파 값을 0~1.0f로 1초 동안 전환(페이드인, 아웃)
                    .setDuration(1000)
                    .start()

                currentPosition = next
            }
        }
    }

    // 생명주기에 따라 onStart에서 타이머를 켜주고, onStop과 onDestory에서 타이머가 완전히 종료되도록
    override fun onStop() {
        super.onStop()

        Log.d("PhotoFrame", "onStop!! timer cancel")

        timer?.cancel()
    }

    override fun onStart() {
        super.onStart()
        Log.d("PhotoFrame", "onStart!! timer start")

        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("PhotoFrame", "onDestroy!! timer cancel")

        timer?.cancel()
    }
}