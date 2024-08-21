package com.plogging.app.common

import android.util.Log
import com.google.firebase.auth.FirebaseAuthException

class FirebaseAuthErrorHelper {
    companion object {
        fun getErrorMessage(exception: FirebaseAuthException?): String {
            Log.d("exception",exception?.errorCode.toString())
            return when (exception?.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> "이미 사용중인 계정입니다."
                "ERROR_INVALID_EMAIL" -> "올바른 이메일 주소의 형식을 입력하세요."
                "ERROR_OPERATION_NOT_ALLOWED" -> "고객사에 문의해주세요."
                "ERROR_WEAK_PASSWORD" -> "비밀번호는 최소 6자리로 입력해주세요."
                "ERROR_NETWORK_REQUEST_FAILED" -> "네트워크 연결 오류입니다."
                "ERROR_TOO_MANY_REQUESTS" -> "비정상적인 요청입니다. 다시 시도해주세요."
                "ERROR_INTERNAL_ERROR" -> "내부 서버 오류가 발생했습니다. 고객사에 문의해주세요."
                "ERROR_USER_DISABLED" -> "해당 계정은 관리자에 의해 비활성화 되었습니다."
                "ERROR_USER_TOKEN_EXPIRED", "ERROR_INVALID_USER_TOKEN" -> "사용자의 인증 토큰이 만료되었습니다. 다시 로그인 해주세요."
                "ERROR_USER_NOT_FOUND" -> "계정이 존재하지 않습니다."
                "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "동일한 이메일 주소로 계정이 존재합니다."
                "ERROR_WRONG_PASSWORD" -> "비밀번호가 정확하지 않습니다."
                "ERROR_INVALID_CREDENTIAL"->"이메일 또는 비밀번호 오류입니다."
                else -> "알 수 없는 오류가 발생했습니다. 다시 시도해주세요."
            }
        }
    }
}