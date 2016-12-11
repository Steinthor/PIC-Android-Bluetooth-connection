#include "variables.h"
#include "mcc_generated_files/memory.h"
#include "mcc_generated_files/eusart.h"


uint8_t isEmpty(uint16_t address)
{
    struct var tmp = readFromMemory(address);
    if(tmp.value == 0xFF && tmp.max == 0xFF && tmp.min == 0xFF)
        return 1;
    else
        return 0;
}

void writeToMemory(struct var tmp)
{
    DATAEE_WriteByte(tmp.address, tmp.value); // write to eeprom
    DATAEE_WriteByte(tmp.address+1, tmp.max); // write to eeprom
    DATAEE_WriteByte(tmp.address+2, tmp.min); // write to eeprom
}

struct var readFromMemory(uint16_t address)
{
    struct var tmp;
    tmp.address = address;
    tmp.value = DATAEE_ReadByte(address);
    tmp.max = DATAEE_ReadByte(address+1);
    tmp.min = DATAEE_ReadByte(address+2);
    return tmp;
}

void getValidString(char *buffer)
{
    uint8_t data;
    char * oldbuffer = buffer;
    bool wait = true;
    while(EUSART_DataReady || wait)     //check if any data is received
    {
        data = EUSART_Read();	// Read data received
        *buffer = data;
        if(*buffer == '>')
        {
            wait = false;
        }
        if(*oldbuffer == '<')
            buffer++;  // Increment the string pointer
        
    }
}

int convertCharToInt (char *buffer)
{
    int modifier = 10000;
    int value = 0;
    for(int j = 0; j < 5; j++)
    {
        value = value + (buffer[j]-48)*modifier;
        modifier = modifier/10;
    }   
    return value;
}