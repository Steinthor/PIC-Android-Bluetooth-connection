/* 
 * File:   variables.h
 * Author: stan
 *
 * Created on December 6, 2016, 10:46 PM
 */

#ifndef VARIABLES_H
#define	VARIABLES_H

#ifdef	__cplusplus
extern "C" {
#endif

#include <stdint.h>
    
/*
 * data structure of a single variable.
 * address: the address of the variable
 * value: the current value of the variable
 * max: the maximum value of the variable
 * min: the minimum value of the variable
 */
struct var {
    uint16_t address;
    uint8_t value;
    uint8_t max;
    uint8_t min;
};

/*
 * counter of total number of variables
 */
uint8_t varcounter;

/*
 * Checks if variable is empty, returns true if empty.
 */
uint8_t isEmpty(uint16_t address);

/*
 * takes a single variable structure and writes it to EEPROM memory
 */
void writeToMemory(struct var tmp);

/*
 * returns a variable from EEPROM memory
 */
struct var readFromMemory(uint16_t address);

/*
 *
 */
void getValidString(char *buffer);

/*
 *  Converts char[5] array to an integer
 */
int convertCharToInt (char *buffer);

#ifdef	__cplusplus
}
#endif

#endif	/* VARIABLES_H */

