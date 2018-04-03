" Open and close all the three plugins on the same time 
nmap <F8>   :TrinityToggleAll<CR> 

" Open and close the srcexpl.vim separately 
nmap <F5>   :TrinityToggleSourceExplorer<CR> 

" Open and close the taglist.vim separately 
nmap <F6>  :TrinityToggleTagList<CR> 

" Open and close the NERD_tree.vim separately 
nmap <F7>  :TrinityToggleNERDTree<CR> 

set cursorline
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
" Tlist的内部变量。函数列表。 
"let Tlist_Use_Right_Window=1 
"let Tlist_File_Fold_Auto_Close=1 
" 打开当前目录文件列表 
map <F3> :Explore<CR> 
" 函数和变量列表 
"map <F4> :TlistToggle<CR> 
" 搜索当前词，并打开quickfix窗口 
"map <F5> :call Search_Word()<CR> 
" 全能补全 
"inoremap <F8> <C-x><C-o> 
" 没事，鼠标画线玩的。 
" noremap <F9> :call ToggleSketch()<CR> 
" 启动函数变量快速浏览的时间设置 
set updatetime=100

set number
set shiftwidth=4
"set autoindent


" enable filetype detection:
filetype on

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

