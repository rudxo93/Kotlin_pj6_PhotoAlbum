package com.example.photoalbum

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
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

    private fun initAddPhotoButton() {
        addPhotoButton.setOnClickListener {
            // 권한이 있는지 없는지 체크
            // 특정 분기가 아니라 순차적으로 체크하기 위해서 when사용
            when {
                // 권한이 수락된 상태라면
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 권한이 잘 부여되었다면 갤러리에서 사진을 선택하는 기능
                    navigatePhotos()
                }

                // 권한 수락이 거절되었다면 교육용 팝업을 띄운다.
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    // 교육용 팝업 확인 후 권한팝업을 띄우는 기능
                    showPermissionContextPopup()
                }

                else -> {
                    // 권한을 요청하는 팝업
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                }
            }
        }
    }
}