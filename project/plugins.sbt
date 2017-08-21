// --- 依存ライブラリの取得を高速化
// https://github.com/coursier/coursier
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC3")

// --- 依存ライブラリの関連を見る
// https://github.com/jrudolph/sbt-dependency-graph
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

// --- Scalaコードのフォーマット
// https://github.com/scalameta/scalafmt
// https://github.com/lucidsoftware/neo-sbt-scalafmt
addSbtPlugin("com.lucidchart" % "sbt-scalafmt-coursier" % "1.10")

// --- Scalaコンパイルエラーを見やすく
// https://github.com/Duhemm/sbt-errors-summary
addSbtPlugin("org.duhemm" % "sbt-errors-summary" % "0.6.0")
