package ir.beigirad.metalavaextended

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import javax.inject.Inject

open class MetalavaExtendedExtension @Inject constructor(
    objectFactory: ObjectFactory,
) {
    val ignoreList: ListProperty<String> = objectFactory.listProperty(String::class.java)
        .apply { set(emptyList()) }

    // default = buildDir / "compatibility-plugin" / "$reportPrefix.api-full.txt"
    val reportDir: RegularFileProperty = objectFactory.fileProperty()

    // default = $projectDir / "$reportPrefix.api.txt"
    val filteredReportDir: RegularFileProperty = objectFactory.fileProperty()
}

