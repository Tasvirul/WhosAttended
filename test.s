.data
prompt1: .asciiz "\n\n Enter the first integer please:"
prompt2: .asciiz "Enter the second integer please:"
resulmul: .asciiz "The multiplication result is:"
resuladd: .asciiz "The addition result is:"
resulsub: .asciiz "The subtraction result is:"
resuldiv: .asciiz "The division result is:"
newline: .asciiz "\n"

              .text
main:
    #t0-to hold first integer
    #t1-to hold second integer
    #t2- used to hold the sum of t$1 and t$2
        #first number
        li $v0, 4 #syscall to print string
        la $a0, prompt1  #address of string to print
        syscall
#
        li $v0, 5 #syscall to read an integer
        syscall
        move $t0, $v0  #move the number to read into $t0
    #second number
    li $v0, 4
    la $a0, prompt2
    syscall
#
    li $v0,5        
    syscall
    move $t1,$v0
#
    #print out sum of $t2
    li $v0, 4
    la $a0, resulmul
    syscall
#
    mul $a0, $t1, $t0 #compute the sum
    li $v0, 1
    syscall

    li $v0, 4
    la $a0, newline
    syscall
    
    #print out sum of $t2
    li $v0, 4
    la $a0, resuladd
    syscall

    add $a0, $t1, $t0 #compute the sum
    li $v0, 1
    syscall

    li $v0, 4
    la $a0, newline
    syscall
    
    #print out sum of $t2
    li $v0, 4
    la $a0, resulsub
    syscall

    sub $a0, $t1, $t0 #compute the sum
    li $v0, 1
    syscall

    li $v0, 4
    la $a0, newline
    syscall
    
    #print out sum of $t2
    li $v0, 4
    la $a0, resuldiv
    syscall

    div $a0, $t1, $t0 #compute the sum
    li $v0, 1
    syscall
#
    li $v0, 10
    syscall
