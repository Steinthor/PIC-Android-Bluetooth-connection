/**
  Generated Main Source File

  Company:
    Microchip Technology Inc.

  File Name:
    main.c

  Summary:
    This is the main file generated using MPLAB(c) Code Configurator

  Description:
    This header file provides implementations for driver APIs for all modules selected in the GUI.
    Generation Information :
        Product Revision  :  MPLAB(c) Code Configurator - 4.0
        Device            :  PIC16F18855
        Driver Version    :  2.00
    The generated drivers are tested against the following:
        Compiler          :  XC8 1.35
        MPLAB             :  MPLAB X 3.40
*/

/*
    (c) 2016 Microchip Technology Inc. and its subsidiaries. You may use this
    software and any derivatives exclusively with Microchip products.

    THIS SOFTWARE IS SUPPLIED BY MICROCHIP "AS IS". NO WARRANTIES, WHETHER
    EXPRESS, IMPLIED OR STATUTORY, APPLY TO THIS SOFTWARE, INCLUDING ANY IMPLIED
    WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY, AND FITNESS FOR A
    PARTICULAR PURPOSE, OR ITS INTERACTION WITH MICROCHIP PRODUCTS, COMBINATION
    WITH ANY OTHER PRODUCTS, OR USE IN ANY APPLICATION.

    IN NO EVENT WILL MICROCHIP BE LIABLE FOR ANY INDIRECT, SPECIAL, PUNITIVE,
    INCIDENTAL OR CONSEQUENTIAL LOSS, DAMAGE, COST OR EXPENSE OF ANY KIND
    WHATSOEVER RELATED TO THE SOFTWARE, HOWEVER CAUSED, EVEN IF MICROCHIP HAS
    BEEN ADVISED OF THE POSSIBILITY OR THE DAMAGES ARE FORESEEABLE. TO THE
    FULLEST EXTENT ALLOWED BY LAW, MICROCHIP'S TOTAL LIABILITY ON ALL CLAIMS IN
    ANY WAY RELATED TO THIS SOFTWARE WILL NOT EXCEED THE AMOUNT OF FEES, IF ANY,
    THAT YOU HAVE PAID DIRECTLY TO MICROCHIP FOR THIS SOFTWARE.

    MICROCHIP PROVIDES THIS SOFTWARE CONDITIONALLY UPON YOUR ACCEPTANCE OF THESE
    TERMS.
*/

#include "mcc_generated_files/mcc.h"
#include "variables.h"
#include <stdio.h>

void InitPorts() {
    
    // set PORTA as output:
    TRISA = 0b00000000; // 0 = output, 1 = input
    // write to PORTX by using LATX, avoids errors.
    LATA = 0b00000000;
    // The ANSELA bits default to the Analog mode after Reset.
    // used to configure the Input mode of an I/O pin to analog, 'high' enables analog
    ANSELA = 0b00000000;;    
}

void putch(char d){
    EUSART_Write(d);
}

void InitVars() {
    //reads variables from the EEPROM into the program variables
    // the EEPROM address range 0x00-0xFF is mapped into the FSR address space between 0x7000-0x70FF
    //uint16_t base = 0x7000;
    //uint16_t tmp = base + var1_A

        struct var var1;
        var1.address = 0x7000;
        var1.value = 41;
        var1.max = 255;
        var1.min = 0;
        writeToMemory(var1);
    
    

        struct var var2;
        var2.address = 0x7003;
        var2.value = 42;
        var2.max = 255;
        var2.min = 0;
        writeToMemory(var2);
    
    

        struct var var3;
        var3.address = 0x7006;
        var3.value = 43;
        var3.max = 255;
        var3.min = 0;
        writeToMemory(var3);
    
    
    
}



/*
                         Main application
 */
void main(void)
{
    // initialize the device
    SYSTEM_Initialize();

    // When using interrupts, you need to set the Global and Peripheral Interrupt Enable bits
    // Use the following macros to:

    // Enable the Global Interrupts
    INTERRUPT_GlobalInterruptEnable();

    // Enable the Peripheral Interrupts
    INTERRUPT_PeripheralInterruptEnable();

    // Disable the Global Interrupts
    //INTERRUPT_GlobalInterruptDisable();

    // Disable the Peripheral Interrupts
    //INTERRUPT_PeripheralInterruptDisable();

    InitPorts();
    //InitVars();
    LATAbits.LATA3 = 1;
            
    char check[20];
    char err[] = "picerr";
    struct var led;
    while (1)
    {
         __delay_ms(40);
        
        if(EUSART_DataReady)
        {
            getValidString(check);

            if(check[1] == '5' && check[2] == '0')
            {
                printf("%c", 2);
                for(int i = 0; i < 3; i++)
                {
                    struct var tmp = readFromMemory(0x7000 + i*3);
                    printf("<%u,%u,%u,%u/>", tmp.value, tmp.max, tmp.min,tmp.address);
                }
                    printf("%c", 3);
            }
            else if (check[1] == '6' && check[2] == '1')
            {
                // string: <61,28678,50/>
                char add[5];
                int address;
                int val = 0;
                int modifier = 0;
                struct var temp;
                
                // get the address
                for(int i = 0; i < 5; i++)
                {
                    add[i] = check[4+i];
                }
                address = convertCharToInt(add);
                
                // get the value
                for(int i = 10; i < 20; i++)
                {
                    if(check[i] == '/')
                    {
                        // find the value
                        modifier = (i - 10);
                        switch(modifier)
                        {
                                case 1: modifier = 1;
                                break;
                                case 2: modifier = 10;
                                break;
                                case 3: modifier = 100;
                                break;
                        }
                        for(int j = 10; j < i; j++)
                        {
                            val = val + (check[j]-48)*modifier;
                            modifier = modifier/10;
                        }                        
                        
                        break;
                    }
                }
                printf("ok: %u", val);
                temp = readFromMemory(address);
                temp.value = val;
                writeToMemory(temp);
            }
            else
                printf("%s : %s.", err, check);
            
            // make LEDs light up if value is higher than 128
         // var 1 = led 1, var 2 = led 2, var 3 = led 3
         
         led = readFromMemory(0x7000);
         if (led.value > 127)
             LATAbits.LATA0 = 1;
         else
             LATAbits.LATA0 = 0;
         
         led = readFromMemory(0x7003);
         if (led.value > 127)
             LATAbits.LATA1 = 1;
         else
             LATAbits.LATA1 = 0;
         
         led = readFromMemory(0x7006);
         if (led.value > 127)
             LATAbits.LATA2 = 1;
         else
             LATAbits.LATA2 = 0;
        }
        else if (!EUSART_DataReady)
        {
            if(check[0] != 0)
            {
                for(int i = 0; i < 16;i++)
                {
                    if (check[i] != 0)
                        check[i] = 0;
                    else
                        break;
                }
            }
        }

         
         
         
    }
}
/**
 End of File
*/