rem 実行コマンドを表示しない
@echo off

rem
rem ECTECを実行するためのbatファイル
rem

rem 設定事項 空白間に挟まないこと！！
rem set HOGE="変数の値"
set server=localhost
set database=dbtest_time
set db=%server%/%database%
set repo=repositories-timetest.csv
rem 日付を取得
set YYYYMMDD=%DATE:/=%
set result=result_%YYYYMMDD%
rem このバッチが存在するフォルダをカレントに
pushd %0\..
cls

rem
rem メインの処理
rem

rem 今日の日付の付いた出力結果格納用フォルダを作成
mkdir %result%
echo %repo%

rem STEP0: DB作成
start /b /wait java -Xmx1024M -jar 00_DBMaker.jar -d %db% > %result%/00_DBMakerResult.txt
echo STEP0: DONE(%date%-%time%)

rem STEP1: 分析対象リポジトリの登録
start /b /wait java -Xmx1024M -jar 01_RepositoryResister.jar -d %db% -i %repo% > %result%/01_RepositoryResister.txt
echo STEP1: DONE(%date%-%time%)

rem STEP2: 対象対象リビジョンの検出
start /b /wait java -Xmx1024M -jar 02_RevisionDetector.jar -d %db% > %result%/02_RevisionDetector.txt
echo STEP2: DONE(%date%-%time%)

rem STEP3: 結合リビジョンの特定
start /b /wait java -Xmx1024M -jar 03_Combine.jar -d %db% > %result%/03_Combine.txt
echo STEP3: DONE(%date%-%time%)

rem STEP4: 分析対象リビジョンに存在するファイルを特定
start /b /wait java -Xmx1024M -jar 04_FileDetector.jar -d %db% > %result%/04_FileDetector.txt
echo STEP4: DONE(%date%-%time%)

rem STEP5: 各ファイルについて、含まれるコード片を特定
start /b /wait java -Xmx1024M -jar 05_CodeFragmentDetector.jar -d %db% > %result%/05_CodeFragmentDetector.txt
echo STEP5: DONE(%date%-%time%)

rem STEP6: DBに登録されているコード片から、クローンセットを検出
start /b /wait java -Xmx1024M -jar 06_CloneDetector.jar -d %db% > %result%/06_CloneDetector.txt
echo STEP6: DONE(%date%-%time%)

rem STEP7: 連続するリビジョン間でのコード片のリンクを特定
start /b /wait java -Xmx1024M -jar 07_CodeFragmentLinkDetector.jar -d %db% > %result%/07_CodeFragmentLinkDetector.txt
echo STEP7: DONE(%date%-%time%)

rem STEP8: 連続するリビジョン間でのコードクローンのリンクを特定
start /b /wait java -Xmx1024M -jar 08_CloneSetLinkDetector.jar -d %db% > %result%/08_CloneSetLinkDetector.txt
echo STEP8: DONE(%date%-%time%)

rem STEP9: コード片の系譜を特定
start /b /wait java -Xmx1024M -jar 09_GenealogyDetector.jar -gm f -d %db% > %result%/09_CodeFragmentGenealogyDetector.txt
echo STEP9: DONE(%date%-%time%)

rem STEP10: クローンセットの系譜を特定
start /b /wait java -Xmx1024M -jar 09_GenealogyDetector.jar -gm c -d %db% > %result%/10_CloneGenealogyDetector.txt
echo STEP10: DONE(%date%-%time%)

rem 終了処理

pause
exit
