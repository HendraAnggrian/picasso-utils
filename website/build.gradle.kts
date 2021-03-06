plugins {
    `git-publish`
}

gitPublish {
    repoUri.set(RELEASE_WEBSITE)
    branch.set("gh-pages")
    contents.from(
        "src",
        "../$RELEASE_ARTIFACT-commons/build/docs",
        "../$RELEASE_ARTIFACT-transformations/build/docs",
        "../$RELEASE_ARTIFACT-palette/build/docs")
}

tasks["gitPublishCopy"].dependsOn(
    ":$RELEASE_ARTIFACT-commons:dokka",
    ":$RELEASE_ARTIFACT-transformations:dokka",
    ":$RELEASE_ARTIFACT-palette:dokka")
