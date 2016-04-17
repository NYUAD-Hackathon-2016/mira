
#include <stdio.h>
#include <avr/io.h>
#include <avr/interrupt.h>

#ifndef F_CPU
#warning "F_CPU was not defined, defining it now as 16000000"
#define F_CPU 16000000UL
#endif
#define BAUD 9600UL      // baud rate
// Calculations
#define UBRR_VAL ((F_CPU+BAUD*8)/(BAUD*16)-1)   // smart rounding
#define BAUD_REAL (F_CPU/(16*(UBRR_VAL+1)))     // real baud rate
#define BAUD_ERROR ((BAUD_REAL*1000)/BAUD) // error in parts per mill, 1000 = no error
#if ((BAUD_ERROR<990) || (BAUD_ERROR>1010))
#error Error in baud rate greater than 1%!
#endif

void uart_init(void) {
	UBRR0H = UBRR_VAL >> 8;
	UBRR0L = UBRR_VAL & 0xFF;
	UCSR0C = (0 << UMSEL01) | (0 << UMSEL00) | (1 << UCSZ01) | (1 << UCSZ00); // asynchron 8N1
	UCSR0B |= (1 << RXEN0); // enable UART RX
	UCSR0B |= (1 << TXEN0); // enable UART TX
	UCSR0B |= (1 << RXCIE0); //interrupt enable
}

/* Receive symbol, not necessary for this example, using interrupt instead*/
uint8_t uart_getc(void) {
	while (!(UCSR0A & (1 << RXC0)))
		// wait until symbol is ready
		;
	return UDR0; // return symbol
}

uint8_t uart_putc(unsigned char data) {
	/* Wait for empty transmit buffer */
	while (!(UCSR0A & (1 << UDRE0)))
		;
	/* Put data into buffer, sends the data */
	UDR0 = data;
	return 0;
}


void initIO(void) {
	DDRD |= (1 << DDD3);
	DDRB = 0xff; //all out
}


volatile uint8_t data = 10;
int counter = 0;
int  r = 0, g = 0,b = 0;

int interrupt = 0;
ISR(USART_RX_vect) { // the interrupt service routine
	data = UDR0;//UDR0 needs to be read
  switch (counter){
  case 0:
  r = data;
  break;
  case 1:
  g = data;
  break;
  case 2:
  b = data;
  break;
  }
  counter = (counter+1)%3;
 
    interrupt = !interrupt;
}   


void initSerial(){
        initIO();
	uart_init();
	sei();
}
const int buttonPin =2 ;
volatile int buttonState = 0; 
typedef struct {
  unsigned char red;
  unsigned char green;
  unsigned char blue;
} led_state_t; 

led_state_t LEDState;
int pinA = 3;
int pinB = 6;
int pinC = 9;
void initRGB(void) {
  pinMode(pinA, OUTPUT);
  pinMode(pinB, OUTPUT);
  pinMode(pinC, OUTPUT);
  setRGB(0, 0, 0);
}

void setRGB(unsigned char red, unsigned char green, unsigned char blue) {
  analogWrite(pinA, red);
  analogWrite(pinB, green);
  analogWrite(pinC, blue);
}

void setup() {
  initSerial();
  initRGB();
  pinMode(buttonPin, INPUT);
  // Attach an interrupt to the ISR vector
  attachInterrupt(0, pin_ISR, CHANGE);
  
}
int sound;
int randNumber;
void loop() {
  sound = analogRead(A0);
  if (interrupt)
    setRGB(sound ,sound ,sound);
  else
    setRGB(0 ,0 ,0);
  
}
void pin_ISR() {
  buttonState = digitalRead(buttonPin);
  uart_putc(buttonState  );
  
  
}
