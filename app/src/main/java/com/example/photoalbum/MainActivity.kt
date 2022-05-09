package com.example.photoalbum

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val addPhotoButton: Button by lazy {
        findViewById(R.id.addPhotoButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddPhotoButton()
    }

    /*
        사진 추가 버튼 누를경우 when문을 사용해서 순차적검사
        권한이 수락된 상태 -> 사진은 선택하는 기능 실행
        권한이 거절 -> 교육용 팝업을 띄워 왜 이러한 권한이 필요한지 유저한테 설명 후 권한을 다시 요청
    */
    private fun initAddPhotoButton() {
        addPhotoButton.setOnClickListener {
            // 권한이 있는지 없는지 체크
            // 특정 분기가 아니라 순차적으로 체크하기 위해서 when사용
            when {
                // 권한이 수락된 상태라면
                ContextCompat.checkSelfPermission( // 1. 사용할 권한이 주어졌는지 확인
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 권한이 잘 부여되었다면 갤러리에서 사진을 선택하는 기능
                    navigatePhotos()
                }

                // 권한 수락이 거절되었다면 교육용 팝업을 띄운다.
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> { // 3. 거정당했다면 도움말을 띄운다.
                    // 교육용 팝업 확인 후 권한팝업을 띄우는 기능
                    showPermissionContextPopup()
                }

                else -> {
                    // 권한을 요청하는 팝업
                    requestPermissions( // 2. 요청할 권한을 배열로 담아서 요청 후
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                }
            }
        }
    }

    // 경고메세지를 보여주고 사용자가 동의할 경우 다시한번 권한요청을 하는 창(requestCode는 1000으로 설정했다.)
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다")
            .setMessage("전자액자 앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                // 권한을 요청하는 팝업
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기"){_, _ -> }
            .create()
            .show()
    }
}