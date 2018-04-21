" 打开当前目录文件列表 
"map <F3> :Explore<CR> 
" 函数和变量列表 
"map <F4> :TlistToggle<CR> 
" 搜索当前词，并打开quickfix窗口 
"map <F5> :call Search_Word()<CR>
" 全能补全 
"inoremap <F8> <C-x><C-o> 
" 没事，鼠标画线玩的。 
" noremap <F9> :call ToggleSketch()<CR> 

" Open and close all the three plugins on the same time 
nmap <F8>   :TrinityToggleAll<CR> 

" Open and close the srcexpl.vim separately 
nmap <F5>   :TrinityToggleSourceExplorer<CR> 

" Open and close the taglist.vim separately 
nmap <F6>  :TrinityToggleTagList<CR> 

" Open and close the NERD_tree.vim separately 
nmap <F7>  :TrinityToggleNERDTree<CR> 

syntax on 
" 设置文字编码自动识别 
set fencs=utf-8,cp936 
" 使用鼠标 
set mouse=a
"set mouse=v 
" 设置高亮搜索 
set hlsearch 
" 输入字符串就显示匹配点 
set incsearch 
" 输入的命令显示出来，看的清楚些。 
set showcmd 
" 启动函数变量快速浏览的时间设置 
set updatetime=100

set number
set shiftwidth=4
"set autoindent

" recognize anything in my .Postponed directory as a news article, and anything
" at all with a .txt extension as being human-language text [this clobbers the
" `help' filetype, but that doesn't seem to prevent help from working
" properly]:
augroup filetype
  autocmd BufNewFile,BufRead */.Postponed/* set filetype=mail
  autocmd BufNewFile,BufRead *.txt set filetype=human
augroup END

" in human-language files, automatically format everything at 72 chars:
autocmd FileType mail,human set formatoptions+=t textwidth=72

" for C-like programming, have automatic indentation:
 autocmd FileType c,cpp,java,slang set cindent
 autocmd FileType c,cpp,java,slang set tabstop=4
autocmd FileType c,cpp,java,slang set textwidth=78
autocmd FileType c,cpp,java,slang set et
autocmd FileType c,cpp,java,slang set smarttab
set cino=:0,t0,+4,(0,u0,l1,c0)

" for actual C (not C++) programming where comments have explicit end
" characters, if starting a new line in the middle of a comment automatically
" insert the comment leader characters:
autocmd FileType c set formatoptions+=ro

" for Perl programming, have things in braces indenting themselves:
autocmd FileType perl set smartindent

" for CSS, also have things in braces indented:
autocmd FileType css set smartindent

" for HTML, generally format text, but if a long line has been created leave it
" alone when editing:
autocmd FileType html set formatoptions+=tl

" for both CSS and HTML, use genuine tab characters for indentation, to make
" files a few bytes smaller:
autocmd FileType html,css set noexpandtab tabstop=8

" in makefiles, don't expand tabs to spaces, since actual tab characters are
" needed, and have indentation at 8 chars to be sure that all indents are tabs
" (despite the mappings later):
 autocmd FileType make set noexpandtab shiftwidth=8

" * Search & Replace

" make searches case-insensitive, unless they contain upper-case letters:
" set ignorecase
 set smartcase


 
 
" ========================================================== 
":shell
"切换到shell里（此时并没有退出vim，可以理解成vim转入后台），你可以在shell中做任何操作，退出shell（比如用exit）后，会切换回原来的vim中

":!cmd
"不退出vim 执行命令 cmd

":r !cmd
"不退出vim执行命令cmd,并将cmd的输出内容插入当前文本中。
 
" ==========================================================
set ignorecase
set softtabstop=4
set tabstop=4
set expandtab
set clipboard=autoselect,exclude:cons\\\|linux\\\|screen
set wildmenu
set showmatch

filetype indent plugin on
set nocp
set completeopt=longest,menu
set guioptions+=a


" ==========================================================
"let &termencoding=&encoding
"set encoding=utf-8
"set fileencoding=utf-8
"set fileencodings=ucs-bom,utf-8,chinese

" ==========================================================
let g:SuperTabRetainCompletionType=2
" let g:SuperTabDefaultCompletionType="<C-X><C-O>"
" let g:SuperTabDefaultCompletionType="context"

" ==========================================================
function! My_ChangeToFilePath()  
        try  
                if exists("g:last_work_path")  
                        let g:last_last_work_path = g:last_work_path  
                endif  
        catch  
                echo v:errmsg  
        endtry  
        let g:last_work_path = escape(getcwd(), " ")  
        let str = expand("%:p:h")  
        let str = s:Escape(str)  
        "let
        "str
        "=
        "escape(str,
        "" ")  
        execute "cd ".str  
endfunction  

let mapleader=","   "mapleader默认值是/
"nmap <leader>w :w<CR>   "按,w保存文件
nmap <silent> <leader>cd :call My_ChangeToFilePath()<cr>  
" =================================================================================
nmap <F1> :cs find s <C-R>=expand("<cword>")<CR><CR>
nmap <F2> :cs find c <C-R>=expand("<cword>")<CR><CR>

nmap <F3> :A<CR>
nnoremap <silent> <F4> :Grep<CR>

"--c++-kinds=+p  : 为C++文件增加函数原型的标签
"--fields=+iaS   : 在标签文件中加入继承信息(i)、类成员的访问控制信息(a)、以及函数的指纹(S)
"--extra=+q      : 为标签增加类修饰符。注意，如果没有此选项，将不能对类成员补全
map <F9> :!ctags -R --c++-kinds=+p --fields=+iaS --extra=+q .<CR>
map <F10> :Tlist<CR>
map <F11> :!cscope -Rbq -f /home/liqiongw/sourceCode/androidm_base/vendor/intel/camera/camera3hal/cscope.out

map <F12> :NERDTree<CR>
nmap wm :WMToggle<CR>

" =================================================================================
set tags=tags;/  
set autochdir

let Tlist_Ctags_Cmd="/usr/bin/ctags" "将taglist与ctags关联
let Tlist_Show_One_File=1    "不同时显示多个文件的tag，只显示当前文件的
let Tlist_Exit_OnlyWindow=1  "如果taglist窗口是最后一个窗口，则退出vim
let Tlist_Use_Right_Window = 1         "在右侧窗口中显示taglist窗口
"let Tlist_Use_SingleClick= 1    " 缺省情况下，在双击一个tag时，才会跳到该tag定义的位置
"let Tlist_Auto_Open=1    "在启动VIM后，自动打开taglist窗口
let Tlist_Process_File_Always=1  "taglist始终解析文件中的tag，不管taglist窗口有没有打开
let Tlist_File_Fold_Auto_Close=1 "同时显示多个文件中的tag时，可使taglist只显示当前文件tag，其它文件的tag都被折叠起来
let Tlist_Inc_Winwidth=0  "1:处理;0:不处理OD

" cs add /home/liqiongw/sourceCode/onel/kernel/modules/camera/cscope.out /home/liqiongw/sourceCode/onel/kernel/modules/camera/
cs add cscope.out 
" =================================================================================
let g:miniBufExplMapWindowNavVim = 1 
let g:miniBufExplMapWindowNavArrows = 1 
let g:miniBufExplMapCTabSwitchBufs = 1 
let g:miniBufExplModSelTarget = 1
let g:miniBufExplMoreThanOne=0

let g:NERDTree_title="[NERDTree]"  
let g:winManagerWindowLayout="NERDTree|TagList|FileExplorer" "设置我们要管理的插件 

function! NERDTree_Start()  
		exec 'NERDTree'  
endfunction  

function! NERDTree_IsValid()  
		return 1  
endfunction  


