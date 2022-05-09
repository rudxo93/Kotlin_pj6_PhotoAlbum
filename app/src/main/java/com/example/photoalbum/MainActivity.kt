package com.example.photoalbum

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val addPhotoButton: Button by lazy {
        findViewById(R.id.addPhotoButton)
    }

    private val startPhotoFrameModeButton: Button by lazy {
        findViewById(R.id.startPhotoFrameModeButton)
    }

    // imageView 리스트
   private val imageViewList: List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(findViewById(R.id.imageView11))
            add(findViewById(R.id.imageView12))
            add(findViewById(R.id.imageView13))

            add(findViewById(R.id.imageView21))
            add(findViewById(R.id.imageView22))
            add(findViewById(R.id.imageView23))
        }
    }

    private val imageUriList: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddPhotoButton()
        initStartPhotoFrameModeButton()
    }

    private fun initStartPhotoFrameModeButton() {
        startPhotoFrameModeButton.setOnClickListener {
            val intent = Intent(this, PhotoFrameActivity::class.java)

            // 인텐트에 데이터 담아서 실행
            imageUriList.forEachIndexed { index, uri ->
                intent.putExtra("photo${index}", uri.toString())
            }
            intent.putExtra("photoListSize", imageUriList.size)

            startActivity(intent) // 사진액자 엑티비티 실행
        }
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

    // 권한이 수락, 거절되면 호출
    // override하여 권한을 수락, 획득한 경우 사진을 탐색하는 기능이 수행
    // 거부 시 간단한 토스트메세지를 띄워주도록
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 부여 됬을 때
                    navigatePhotos()
                } else {
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {

            }
        }
    }

    private fun navigatePhotos() {
        // SAF 기능 사용하여 사진 가져오기
        // Intent.ACTION_GET_CONTENT : SAF 기능을 실행시켜서 컨텐츠를 가져오는 (안드로이느 내장)엑티비티를 실행
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*" // 모든 이미지 타입들만 설정 (필터링)
        startActivityForResult(intent, 2000) // 선택된 컨텐츠를 콜백을 통해 받아오려고 (onActivityResult)
        // startActivityForResult : 다음 엑티비티(현재 우리 메인 엑티비티)에 넘겨주기 위해
    }

    // SAF를 통해 사용자가 내놓은 결과를 그대로 받아서 처리(requestCode 2000번 사용)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // 사용자가 사진을 선택한 경우, 켄슬한 경우 모두 처리
        super.onActivityResult(requestCode, resultCode, data)

        // ok가 아닐 경우는 그냥 반환(취소 등 했을때)
        if(resultCode != Activity.RESULT_OK) {
            return
        }

        when(requestCode) {
            2000 -> {
                val selectedImageUri: Uri? = data?.data

                if(selectedImageUri != null) {
                    if(imageUriList.size == 6) { // 현재 앱에서는 선택가능한 이미지를 최대 6개로 제한, 초과선택하는 경우 토스트메세지
                        Toast.makeText(this, "이미 사진이 가득 찼습니다.", Toast.LENGTH_SHORT).show()
                        return
                    }
                     // 아닌 경우에만 전역으로 선언해둔 image uri리스트에 추가
                    imageUriList.add(selectedImageUri)
                    imageViewList[imageUriList.size - 1].setImageURI(selectedImageUri) // 이미지 추가
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}