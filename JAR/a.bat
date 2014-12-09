rem 実行コマンドを表示しない
@echo off

rem
rem ECTECを実行するためのbatファイル
rem

rem 設定事項
set server=localhost
set database=dbtest1
set db=localhost/dbtest1
set repo=repositories-test.csv
set result=result-%date%

rem このバッチが存在するフォルダをカレントに
pushd %0\..
cls

rem
rem メインの処理
rem

echo test1
echo %date%
echo %0
echo %server%
echo %result%

rem 出力結果格納ディレクトリ作成
mkdir %database%

echo test2

rem 終了処理

pause
exit