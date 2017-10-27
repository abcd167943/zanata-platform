package org.zanata.action

import com.nhaarman.mockito_kotlin.given
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.File
import java.lang.IllegalStateException
import javax.servlet.ServletContext

class FrontendActionTest {
    val manifestText = """
{
  "editor.css": "editor.c6d86c21.cache.css",
  "editor.js": "editor.c6d86c21.cache.js",
  "frontend.css": "frontend.e8af8b0a.cache.css",
  "frontend.js": "frontend.e8af8b0a.cache.js",
  "frontend.legacy.css": "frontend.legacy.a31f9460.cache.css",
  "frontend.legacy.js": "frontend.legacy.a31f9460.cache.js",
  "intl-polyfill.js": "intl-polyfill.04142315.cache.js",
  "runtime.js": "runtime.e8b751b7.cache.js"
}
    """

    private val manifestParent = File(Thread.currentThread().contextClassLoader.getResource(".")?.file, "META-INF/resources")
    private val manifestFile: File = File(manifestParent, "manifest.json")

    @Mock
    private lateinit var servletContext: ServletContext

    private lateinit var frontendAction: FrontendAction

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        given(servletContext.contextPath).willReturn("")
    }

    @After
    fun cleanUp() {
        manifestFile.delete()
    }

    private fun createManifestFile(manifestText: String) {
        manifestParent.mkdirs()
        assertThat(manifestFile.createNewFile()).isTrue()
        manifestFile.writeText(manifestText)
    }

    @Test
    fun canGetAllFrontendJsAndCss() {
        createManifestFile(manifestText)

        frontendAction = FrontendAction(servletContext)

        assertThat(frontendAction.frontendJs).isEqualTo("/frontend.e8af8b0a.cache.js")
        assertThat(frontendAction.frontendCss).isEqualTo("/frontend.e8af8b0a.cache.css")
        assertThat(frontendAction.editorJs).isEqualTo("/editor.c6d86c21.cache.js")
        assertThat(frontendAction.editorCss).isEqualTo("/editor.c6d86c21.cache.css")
        assertThat(frontendAction.legacyJs).isEqualTo("/frontend.legacy.a31f9460.cache.js")
        assertThat(frontendAction.runtime).isEqualTo("/runtime.e8b751b7.cache.js")
    }

    @Test
    fun willThrowExceptionIfManifestFileIsNotFound() {
        assertThatThrownBy { FrontendAction(servletContext) }
                .isInstanceOf(IllegalStateException::class.java)
                .hasMessage("can not load manifest.json from META-INF/resources/manifest.json. Did you forget to build and include zanata frontend?")
    }
}
