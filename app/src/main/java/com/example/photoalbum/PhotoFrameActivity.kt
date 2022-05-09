package com.example.photoalbum

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PhotoFrameActivity: AppCompatActivity() {

    private val photoList = mutableListOf<Uri>() // 변경 가능한 Uri리스트 전역으로 선언

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
}