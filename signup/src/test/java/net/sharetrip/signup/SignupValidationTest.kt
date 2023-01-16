package net.sharetrip.signup

import com.google.common.truth.Truth.assertThat
import net.sharetrip.shared.utils.isEmailValid
import net.sharetrip.shared.utils.isPasswordValid
import org.junit.Test

class SignupValidationTest {

    @Test
    fun `valid email returns true`() {
        val check = "ava@gmail.com".isEmailValid()
        assertThat(check).isTrue()
    }

    @Test
    fun `less than 3 length first email part returns false`() {
        val check = "XY@gmail.com".isEmailValid()
        assertThat(check).isFalse()
    }

    @Test
    fun `more than 30 letter will return false`(){
        val check = "asdhadfjdfhjsdgffugahgdsegdeshgdfgfggguydfueyehedjxaioduiweuyd@gmail.com".isEmailValid()
        assertThat(check).isFalse()
    }

    @Test
    fun `empty email should return false`(){
        val check = "".isEmailValid()
        assertThat(check).isFalse()
    }

    @Test
    fun `empty @ symbol will return false`(){
        val check = "saiful".isEmailValid()
        assertThat(check).isFalse()
    }

    @Test
    fun `empty password will return false`(){
        val check = "".isPasswordValid(8)
        assertThat(check).isFalse()
    }

    @Test
    fun `less than 8 char long password will return false`(){
        val check = "123456".isPasswordValid(8)
        assertThat(check).isFalse()
    }

    @Test
    fun `min 8 char long password will return true`(){
        val check = "12345678".isPasswordValid(8)
        assertThat(check).isTrue()
    }
}