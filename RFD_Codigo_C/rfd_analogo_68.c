 /*
    Microchip ZigBee Stack

    Demo RFD

    This demonstration shows how a ZigBee RFD can be set up.  This demo allows
    the PICDEM Z Demostration Board to act as either a "Switching Load Controller"
    (e.g. a light) or a "Switching Remote Control" (e.g. a switch) as defined by
    the Home Controls, Lighting profile.  It is designed to interact with a
    second PICDEM Z programmed with the Demo Coordinator project.

    To give the PICDEM Z "switch" capability, uncomment the I_AM_SWITCH definition
    below.  To give the PICDEM Z "light" capability, uncomment the I_AM_LIGHT
    definition below.  The PICDEM Z may have both capabilities enabled.  Be sure
    that the corresponding Demo Coordinator device is programmed with complementary
    capabilities.  NOTE - for simplicity, the ZigBee simple descriptors for this
    demonstration are fixed.

    If this node is configured as a "switch", it can discover the network address
    of the "light" using two methods.  If the USE_BINDINGS definition is
    uncommented below, then End Device Binding must be performed between the
    "switch" and the "light" before messages can be sent and received successfully.
    If USE_BINDINGS is commented out, then the node will default to the probable
    network address of the other node, and messages may be able to be sent
    immediately.  However, the node will also be capable of performing Device
    Discovery to discover the actual network address of the other node, in case
    the network was formed with alternate short address assignments.  NOTE: The
    USE_BINDINGS definition must be the same in both the RFD and the ZigBee
    Coordinator nodes.

    Switch functionality is as follows:
        RB4, I_AM_SWITCH defined, sends a "toggle" message to the other node's "light"
        RB4, I_AM_SWITCH not defined, no effect
        RB5, USE_BINDINGS defined, sends an End Device Bind request
        RB5, USE_BINDINGS undefined, sends a NWK_ADDR_req for the MAC address specified

    End Device Binding
    ------------------
    If the USE_BINDINGS definition is uncommented, the "switch" will send an
    APS indirect message to toggle the "light".  In order for the message to
    reach its final destination, a binding must be created between the "switch"
    and the "light".  To do this, press RB5 on one PICDEM Z, and then press RB5
    on the other PICDEM Z within 5 seconds.  A message will be displayed indicating
    if binding was successful or not.  Note that End Device Binding is a toggle
    function.  Performing the operation again will unbind the nodes, and messages
    will not reach their final destination.

    Device Discovery
    ----------------
    If the USE_BINDINGS definition is not uncommented, pressing RB5 will send a
    broadcast NWK_ADDR_req message.  The NWK_ADDR_req message contains the MAC
    address of the desired node.  Be sure this address matches the address
    contained in the other node's zigbee.def file.

    NOTE: To speed network formation, ALLOWED_CHANNELS has been set to
    channel 12 only.

 *********************************************************************
 * FileName:        RFD.c
 * Dependencies:
 * Processor:       PIC18F
 * Complier:        MCC18 v3.00 or higher
 * Company:         Microchip Technology, Inc.
 *
 * Software License Agreement
 *
 * Copyright © 2004-2007 Microchip Technology Inc.  All rights reserved.
 *
 * Microchip licenses to you the right to use, copy and distribute Software 
 * only when embedded on a Microchip microcontroller or digital signal 
 * controller and used with a Microchip radio frequency transceiver, which 
 * are integrated into your product or third party product (pursuant to the 
 * sublicense terms in the accompanying license agreement).  You may NOT 
 * modify or create derivative works of the Software.  
 *
 * If you intend to use this Software in the development of a product for 
 * sale, you must be a member of the ZigBee Alliance.  For more information, 
 * go to www.zigbee.org.
 *
 * You should refer to the license agreement accompanying this Software for 
 * additional information regarding your rights and obligations.
 *
 * SOFTWARE AND DOCUMENTATION ARE PROVIDED “AS IS” WITHOUT WARRANTY OF ANY 
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION, ANY WARRANTY 
 * OF MERCHANTABILITY, TITLE, NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR 
 * PURPOSE. IN NO EVENT SHALL MICROCHIP OR ITS LICENSORS BE LIABLE OR OBLIGATED 
 * UNDER CONTRACT, NEGLIGENCE, STRICT LIABILITY, CONTRIBUTION, BREACH OF 
 * WARRANTY, OR OTHER LEGAL EQUITABLE THEORY ANY DIRECT OR INDIRECT DAMAGES OR 
 * EXPENSES INCLUDING BUT NOT LIMITED TO ANY INCIDENTAL, SPECIAL, INDIRECT, 
 * PUNITIVE OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST DATA, COST OF 
 * PROCUREMENT OF SUBSTITUTE GOODS, TECHNOLOGY, SERVICES, OR ANY CLAIMS BY 
 * THIRD PARTIES (INCLUDING BUT NOT LIMITED TO ANY DEFENSE THEREOF), OR OTHER 
 * SIMILAR COSTS.
 *
 * Author               Date    Comment
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * DF/KO                01/09/06 Microchip ZigBee Stack v1.0-3.5
 * DF/KO                08/31/06 Microchip ZigBee Stack v1.0-3.6
 * DF/KO/YY				11/27/06 Microchip ZigBee Stack v1.0-3.7
 * DF/KO/YY				01/12/07 Microchip ZigBee Stack v1.0-3.8
 ********************************************************************/


//******************************************************************************
// Header Files
//******************************************************************************

// Include the main ZigBee header file.
#include "zigbee.h"
#include "zigbee.def"
#include "zAPL.h"
#include <adc.h>
#include <string.h>
#include <timers.h>

// If you are going to send data to a terminal, include this file.
#include "console.h"


//******************************************************************************
// Configuration Bits
//******************************************************************************

#if defined(MCHP_C18) && defined(__18F4620)
    #pragma romdata CONFIG1H = 0x300001
    const rom unsigned char config1H = 0b00000110;      // HSPLL oscillator

    #pragma romdata CONFIG2L = 0x300002
    const rom unsigned char config2L = 0b00011111;      // Brown-out Reset Enabled in hardware @ 2.0V, PWRTEN disabled

    #pragma romdata CONFIG2H = 0x300003
    const rom unsigned char config2H = 0b00010010;      // HW WD disabled, 1:512 prescaler

    #pragma romdata CONFIG3H = 0x300005
    const rom unsigned char config3H = 0b10000000;      // PORTB digital on RESET

    #pragma romdata CONFIG4L = 0x300006
    const rom unsigned char config4L = 0b10000001;      // DEBUG disabled,
                                                        // XINST disabled
                                                        // LVP disabled
                                                        // STVREN enabled

    #pragma romdata

#elif defined(HITECH_C18) && defined(_18F4620)
    // Set configuration fuses for HITECH compiler.
    __CONFIG(1, 0x0600);    // HSPLL oscillator
    __CONFIG(2, 0x101F);    // PWRTEN disabled, BOR enabled @ 2.0V, HW WD disabled, 1:128 prescaler
    __CONFIG(3, 0x8000);    // PORTB digital on RESET
    __CONFIG(4, 0x0081);    // DEBUG disabled,
                            // XINST disabled
                            // LVP disabled
                            // STVREN enabled
#endif

//******************************************************************************
// Compilation Configuration
//******************************************************************************

//#define USE_BINDINGS
#define I_AM_LIGHT
#define I_AM_SWITCH

//******************************************************************************
// Constants
//******************************************************************************

// Switches and LEDs locations.
#define BROADCAST_SWITCH            PORTBbits.RB5
#define LIGHT_SWITCH                PORTBbits.RB4

#if defined(__18F4620)
	#define BIND_INDICATION             LATAbits.LATA0
	#define MESSAGE_INDICATION          LATAbits.LATA1
	#define MESSAGE_ANALOGO				PORTAbits.RA3
	#define MESSAGE_PUERTA				PORTAbits.RA5
	#define MESSAGE_MOVIMIENTO			PORTAbits.RA4
#else
	#define BIND_INDICATION				LATDbits.LATD0
	#define MESSAGE_INDICATION			LATDbits.LATD1
	//#define MESSAGE_PUERTA				LATDbits.LATD1
#endif

#define BIND_STATE_BOUND            0
#define BIND_STATE_TOGGLE           1
#define BIND_STATE_UNBOUND          1
#define BIND_WAIT_DURATION          (6*ONE_SECOND)

#define LIGHT_OFF                   0x01
#define LIGHT_ON                    0xFF
#define LIGHT_TOGGLE                0xF0
#define LIGHT_INFO	                0x0F
#define PUERTA_INFO	                0xAF
#define MOVIMIENTO_INFO	            0xCF
#define PUERTA_CERRADA				0x01
#define PUERTA_ABIERTA				0xFF
#define MOVIMIENTO_OFF				0x01
#define MOVIMIENTO_ON				0xFF
#define PERSIANA_ABIERTA            0x01
#define PERSIANA_CERRADA            0xFF
#define PERSIANA_CAMBIO            	0xF0
#define PERSIANA_INFO	            0x0F
#define ANALOGO_INFO	            0x0F


#define VERSION_CONFIRM 			(rom char *) "20080314000v0001 WiLAB 14/3/2008\r\n"
//******************************************************************************
// Application Variables
//******************************************************************************

static union
{
    struct
    {
        BYTE    bBroadcastSwitchToggled    : 1;
        BYTE    bLightSwitchToggled        : 1;
        BYTE    bTryingToBind              : 1;
        BYTE    bIsBound                   : 1;
        BYTE    bDestinationAddressKnown   : 1;
    } bits;
    BYTE Val;
} myStatusFlags;
#define STATUS_FLAGS_INIT       0x00
#define TOGGLE_BOUND_FLAG       0x08

NETWORK_DESCRIPTOR  *currentNetworkDescriptor;
ZIGBEE_PRIMITIVE    currentPrimitive;
SHORT_ADDR          destinationAddress;
NETWORK_DESCRIPTOR  *NetworkDescriptor;
BYTE                orphanTries;


//******************************************************************************
// Function Prototypes
//******************************************************************************

void HardwareInit( void );
BOOL myProcessesAreDone( void );

//******************************************************************************
//******************************************************************************
// Main
//******************************************************************************
//******************************************************************************

BYTE c;
int	DataADC;
BYTE ADC_VAL;
BYTE ROTAR;
BYTE ESTADO_PERSIANA=0;
BYTE i;
BYTE rango=1;


void main(void)
{

BYTE BANDERA_PRESENCIA= MESSAGE_MOVIMIENTO;
BYTE BANDERA_PUERTA=MESSAGE_PUERTA;
BYTE BANDERA_BOMBILLO=MESSAGE_INDICATION;
BYTE BANDERA_ANALOGO=rango;
BYTE BANDERA_PERSIANA=ESTADO_PERSIANA;


BANDERA_PRESENCIA^= 1;
BANDERA_PUERTA^= 1;
BANDERA_BOMBILLO^= 1;
BANDERA_ANALOGO^= 1;
BANDERA_PERSIANA^= 1;

	#if defined(__18F87J10)
        NOP();
        NOP();
        NOP();
        NOP();
        NOP();
        OSCTUNEbits.PLLEN = 1;
        NOP();
        NOP();
        NOP();
        NOP();
        NOP();
    #endif	

    CLRWDT();
    ENABLE_WDT();

    currentPrimitive = NO_PRIMITIVE;
    NetworkDescriptor = NULL;
    orphanTries = 3;

    // If you are going to send data to a terminal, initialize the UART.
    ConsoleInit();

    ConsolePutROMString( (ROM char *)"\r\n\r\n\r\n*************************************\r\n" );
    ConsolePutROMString( (ROM char *)"Microchip ZigBee(TM) Stack - v1.0-3.8\r\n\r\n" );
    ConsolePutROMString( (ROM char *)"ZigBee RFD\r\n\r\n" );
    #if (RF_CHIP == MRF24J40)
        ConsolePutROMString( (ROM char *)"Transceiver-MRF24J40\r\n\r\n" );
    #elif (RF_CHIP==UZ2400)
        ConsolePutROMString( (ROM char *)"Transceiver-UZ2400\r\n\r\n" );
    #elif (RF_CHIP==CC2420)
        ConsolePutROMString( (ROM char *)"Transceiver-CC2420\r\n\r\n" );
    #else
        ConsolePutROMString( (ROM char *)"Transceiver-Unknown\r\n\r\n" );
    #endif

    // Initialize the hardware - must be done before initializing ZigBee.
    HardwareInit();

    // Initialize the ZigBee Stack.
    ZigBeeInit();
    // *************************************************************************
    // Perform any other initialization here
    // *************************************************************************

    myStatusFlags.Val = STATUS_FLAGS_INIT;

    // Default the destination address to the ZigBee Coordinator
    destinationAddress.Val = 0x0000;

    // Initialize the LED's.
    BIND_INDICATION = !myStatusFlags.bits.bIsBound;
  //  MESSAGE_INDICATION = 0;
	//MESSAGE_PUERTA = 0;

    // Enable interrupts to get everything going.
    RCONbits.IPEN = 1;
    INTCONbits.GIEH = 1;
	ROTAR = 0X33;
	ESTADO_PERSIANA=0;

    while (1)
    {
		
        CLRWDT();
        ZigBeeTasks( &currentPrimitive );

        switch (currentPrimitive)
        {
            case NLME_NETWORK_DISCOVERY_confirm:
                currentPrimitive = NO_PRIMITIVE;
                if (!params.NLME_NETWORK_DISCOVERY_confirm.Status)
                {
                    if (!params.NLME_NETWORK_DISCOVERY_confirm.NetworkCount)
                    {
                        ConsolePutROMString( (ROM char *)"No networks found.  Trying again...\r\n" );
                    }
                    else
                    {
                        // Save the descriptor list pointer so we can destroy it later.
                        NetworkDescriptor = params.NLME_NETWORK_DISCOVERY_confirm.NetworkDescriptor;

                        // Select a network to try to join.  We're not going to be picky right now...
                        currentNetworkDescriptor = NetworkDescriptor;

SubmitJoinRequest:
                        // not needed for new join params.NLME_JOIN_request.ScanDuration = ;
                        // not needed for new join params.NLME_JOIN_request.ScanChannels = ;
                        params.NLME_JOIN_request.PANId          = currentNetworkDescriptor->PanID;
                        ConsolePutROMString( (ROM char *)"Network(s) found. Trying to join " );
                        PrintChar( params.NLME_JOIN_request.PANId.byte.MSB );
                        PrintChar( params.NLME_JOIN_request.PANId.byte.LSB );
                        ConsolePutROMString( (ROM char *)".\r\n" );
                        params.NLME_JOIN_request.JoinAsRouter   = FALSE;
                        params.NLME_JOIN_request.RejoinNetwork  = FALSE;
                        params.NLME_JOIN_request.PowerSource    = NOT_MAINS_POWERED;
                        params.NLME_JOIN_request.RxOnWhenIdle   = FALSE;
                        params.NLME_JOIN_request.MACSecurity    = FALSE;
                        currentPrimitive = NLME_JOIN_request;
                    }
                }
                else
                {
                    PrintChar( params.NLME_NETWORK_DISCOVERY_confirm.Status );
                    ConsolePutROMString( (ROM char *)" Error finding network.  Trying again...\r\n" );
                }
                break;

            case NLME_JOIN_confirm:
                currentPrimitive = NO_PRIMITIVE;
                if (!params.NLME_JOIN_confirm.Status)
                {
                    ConsolePutROMString( (ROM char *)"Join successful!\r\n" );

                    // Free the network descriptor list, if it exists. If we joined as an orphan, it will be NULL.
                    while (NetworkDescriptor)
                    {
                        currentNetworkDescriptor = NetworkDescriptor->next;
                        free( NetworkDescriptor );
                        NetworkDescriptor = currentNetworkDescriptor;
                    }
                }
                else
                {
                    PrintChar( params.NLME_JOIN_confirm.Status );

                    // If we were trying as an orphan, see if we have some more orphan attempts.
                    if (ZigBeeStatus.flags.bits.bTryOrphanJoin)
                    {
                        // If we tried to join as an orphan, we do not have NetworkDescriptor, so we do
                        // not have to free it.

                        ConsolePutROMString( (ROM char *)" Could not join as orphan. " );
                        orphanTries--;
                        if (orphanTries == 0)
                        {
                            ConsolePutROMString( (ROM char *)"Must try as new node...\r\n" );
                            ZigBeeStatus.flags.bits.bTryOrphanJoin = 0;
                        }
                        else
                        {
                            ConsolePutROMString( (ROM char *)"Trying again...\r\n" );
                        }
                    }
                    else
                    {
                        ConsolePutROMString( (ROM char *)" Could not join selected network. " );
                        currentNetworkDescriptor = currentNetworkDescriptor->next;
                        if (currentNetworkDescriptor)
                        {
                            ConsolePutROMString( (ROM char *)"Trying next discovered network...\r\n" );
                            goto SubmitJoinRequest;
                        }
                        else
                        {
                            // We ran out of descriptors.  Free the network descriptor list, and fall
                            // through to try discovery again.
                            ConsolePutROMString( (ROM char *)"Cleaning up and retrying discovery...\r\n" );
                            while (NetworkDescriptor)
                            {
                                currentNetworkDescriptor = NetworkDescriptor->next;
                                free( NetworkDescriptor );
                                NetworkDescriptor = currentNetworkDescriptor;
                            }
                        }
                    }
                }
                break;

            case NLME_LEAVE_indication:
                if (!memcmppgm2ram( &params.NLME_LEAVE_indication.DeviceAddress, (ROM void *)&macLongAddr, 8 ))
                {
                    ConsolePutROMString( (ROM char *)"We have left the network.\r\n" );
                }
                else
                {
                    ConsolePutROMString( (ROM char *)"Another node has left the network.\r\n" );
                }
                currentPrimitive = NO_PRIMITIVE;
                break;

            case NLME_RESET_confirm:
                ConsolePutROMString( (ROM char *)"ZigBee Stack has been reset.\r\n" );
                currentPrimitive = NO_PRIMITIVE;
                break;

            case NLME_SYNC_confirm:
                switch (params.NLME_SYNC_confirm.Status)
                {
                    case SUCCESS:
                        // I have heard from my parent, but it has no data for me.  Note that
                        // if my parent has data for me, I will get an APSDE_DATA_indication.
                        ConsolePutROMString( (ROM char *)"No data available.\r\n" );
                        break;

                    case NWK_SYNC_FAILURE:
                        // I cannot communicate with my parent.
                        ConsolePutROMString( (ROM char *)"I cannot communicate with my parent.\r\n" );
                        break;

                    case NWK_INVALID_PARAMETER:
                        // If we call NLME_SYNC_request correctly, this doesn't occur.
                        ConsolePutROMString( (ROM char *)"Invalid sync parameter.\r\n" );
                        break;
                }
                currentPrimitive = NO_PRIMITIVE;
                break;

            case APSDE_DATA_indication:
                {
                    WORD_VAL    attributeId;
                    BYTE        command;
                    BYTE        data;
                    BYTE        dataLength;
                    //BYTE        dataType;
                    BYTE        frameHeader;
                    BYTE        sequenceNumber;
                    BYTE        transaction;
                    BYTE        transByte;

                    currentPrimitive = NO_PRIMITIVE;
                    frameHeader = APLGet();

                    switch (params.APSDE_DATA_indication.DstEndpoint)
                    {
                        case EP_ZDO:
                            ConsolePutROMString( (ROM char *)"  Receiving ZDO cluster " );
                            PrintChar( params.APSDE_DATA_indication.ClusterId );
                            ConsolePutROMString( (ROM char *)"\r\n" );

                            // Put code here to handle any ZDO responses that we requested
                            if ((frameHeader & APL_FRAME_TYPE_MASK) == APL_FRAME_TYPE_MSG)
                            {
                                frameHeader &= APL_FRAME_COUNT_MASK;
                                for (transaction=0; transaction<frameHeader; transaction++)
                                {
                                    sequenceNumber          = APLGet();
                                    dataLength              = APLGet();
                                    transByte               = 1;    // Account for status byte

                                    switch( params.APSDE_DATA_indication.ClusterId )
                                    {

                                        // ********************************************************
                                        // Put a case here to handle each ZDO response that we requested.
                                        // ********************************************************

                                        case NWK_ADDR_rsp:
                                            if (APLGet() == SUCCESS)
                                            {
                                                ConsolePutROMString( (ROM char *)"  Receiving NWK_ADDR_rsp.\r\n" );

                                                // Skip over the IEEE address of the responder.
                                                for (data=0; data<8; data++)
                                                {
                                                    APLGet();
                                                    transByte++;
                                                }
                                                destinationAddress.byte.LSB = APLGet();
                                                destinationAddress.byte.MSB = APLGet();
                                                transByte += 2;
                                                myStatusFlags.bits.bDestinationAddressKnown = 1;
                                            }
                                            break;

                                        #ifdef USE_BINDINGS
                                        case END_DEVICE_BIND_rsp:
                                            switch( APLGet() )
                                            {
                                                case SUCCESS:
                                                    ConsolePutROMString( (ROM char *)" End device bind/unbind successful!\r\n" );
                                                    myStatusFlags.bits.bIsBound ^= TOGGLE_BOUND_FLAG;
                                                    BIND_INDICATION = !myStatusFlags.bits.bIsBound;
                                                    break;
                                                case ZDO_NOT_SUPPORTED:
                                                    ConsolePutROMString( (ROM char *)" End device bind/unbind not supported.\r\n" );
                                                    break;
                                                case END_DEVICE_BIND_TIMEOUT:
                                                    ConsolePutROMString( (ROM char *)" End device bind/unbind time out.\r\n" );
                                                    break;
                                                case END_DEVICE_BIND_NO_MATCH:
                                                    ConsolePutROMString( (ROM char *)" End device bind/unbind failed - no match.\r\n" );
                                                    break;
                                                default:
                                                    ConsolePutROMString( (ROM char *)" End device bind/unbind invalid response.\r\n" );
                                                    break;
                                            }
                                            myStatusFlags.bits.bTryingToBind = 0;
                                            break;
                                        #endif

                                        default:
                                            break;
                                    }

                                    // Read out the rest of the MSG in case there is another transaction.
                                    for (; transByte<dataLength; transByte++)
                                    {
                                        APLGet();
                                    }
                                }
                            }
                            break;

                        // ************************************************************************
                        // Place a case for each user defined endpoint.
                        // ************************************************************************
                        case EP_LIGHT:
                            if ((frameHeader & APL_FRAME_TYPE_MASK) == APL_FRAME_TYPE_KVP)
                            {
                                frameHeader &= APL_FRAME_COUNT_MASK;
                                for (transaction=0; transaction<frameHeader; transaction++)
                                {
                                    sequenceNumber          = APLGet();
                                    command                 = APLGet();
                                    attributeId.byte.LSB    = APLGet();
                                    attributeId.byte.MSB    = APLGet();

                                    //dataType = command & APL_FRAME_DATA_TYPE_MASK;
                                    command &= APL_FRAME_COMMAND_MASK;

                                    if ((params.APSDE_DATA_indication.ClusterId == OnOffSRC_CLUSTER) &&
                                        (attributeId.Val == OnOffSRC_OnOff))
                                    {
                                        if ((command == APL_FRAME_COMMAND_SET) ||
                                            (command == APL_FRAME_COMMAND_SETACK))
                                        {
                                            // Prepare a response in case it is needed.
                                            TxBuffer[TxData++] = APL_FRAME_TYPE_KVP | 1;    // KVP, 1 transaction
                                            TxBuffer[TxData++] = sequenceNumber;
                                            TxBuffer[TxData++] = APL_FRAME_COMMAND_SET_RES | (APL_FRAME_DATA_TYPE_UINT8 << 4);
                                            TxBuffer[TxData++] = attributeId.byte.LSB;
                                            TxBuffer[TxData++] = attributeId.byte.MSB;

                                            // Data type for this attibute must be APL_FRAME_DATA_TYPE_UINT8
                                            data = APLGet();
                                            switch (data)
                                            {
                                                case LIGHT_OFF:
                                                    ConsolePutROMString( (ROM char *)" Turning light off.\r\n" );
                                                    MESSAGE_INDICATION = 0;
                                                    TxBuffer[TxData++] = SUCCESS;
                                                    break;
                                                case LIGHT_ON:
                                                    ConsolePutROMString( (ROM char *)" Turning light on.\r\n" );
                                                    MESSAGE_INDICATION = 1;
                                                    TxBuffer[TxData++] = SUCCESS;
                                                    break;
                                                case LIGHT_TOGGLE:
                                                    ConsolePutROMString( (ROM char *)" Toggling light.\r\n" );
                                                    MESSAGE_INDICATION ^= 1;
                                                    TxBuffer[TxData++] = SUCCESS;
                                                    break;
												case LIGHT_INFO:
                                                    ConsolePutROMString( (ROM char *)" Informando sobre el estado de la luz.\r\n" );
													if(MESSAGE_INDICATION==1){
														ConsolePutROMString((ROM char *)"La luz esta prendida" );
	                                                    TxBuffer[TxData++] = LIGHT_ON;
													}
													if(MESSAGE_INDICATION==0){
														ConsolePutROMString((ROM char *)"La luz esta apagada" );
	                                                    TxBuffer[TxData++] = LIGHT_OFF;
													}
                                                    break;
                                                default:
                                                    PrintChar( data );
                                                    ConsolePutROMString( (ROM char *)" Invalid light message.\r\n" );
                                                    TxBuffer[TxData++] = KVP_INVALID_ATTRIBUTE_DATA;
                                                    break;
                                            }
                                        }
										//Si lo ponemos nunca entra y no transmite los datos
                                        //if (command == APL_FRAME_COMMAND_SETACK)
                                        //{
                                            // Send back an application level acknowledge.
                                            ZigBeeBlockTx();

                                            // Take care here that parameters are not overwritten before they are used.
                                            // We can use the data byte as a temporary variable.
                                            params.APSDE_DATA_request.DstAddrMode = params.APSDE_DATA_indication.SrcAddrMode;
                                            params.APSDE_DATA_request.DstEndpoint = params.APSDE_DATA_indication.SrcEndpoint;
                                            params.APSDE_DATA_request.DstAddress.ShortAddr = params.APSDE_DATA_indication.SrcAddress.ShortAddr;

                                            //params.APSDE_DATA_request.asduLength; TxData
                                            //params.APSDE_DATA_request.ProfileId; unchanged
                                            params.APSDE_DATA_request.RadiusCounter = DEFAULT_RADIUS;
                                            params.APSDE_DATA_request.DiscoverRoute = ROUTE_DISCOVERY_ENABLE;
											#ifdef I_SUPPORT_SECURITY
												params.APSDE_DATA_request.TxOptions.Val = 1;
											#else											                                            
                                            	params.APSDE_DATA_request.TxOptions.Val = 0;
											#endif                                            
                                            params.APSDE_DATA_request.SrcEndpoint = EP_LIGHT;
                                            //params.APSDE_DATA_request.ClusterId; unchanged

                                            currentPrimitive = APSDE_DATA_request;
                                    
                                    }
                                    // TODO read to the end of the transaction.
                                } // each transaction
                            } // frame type
                            break;

////////////INICIO END POINTS PERSONALIZADOS
						case EP_PUERTA:
                            if ((frameHeader & APL_FRAME_TYPE_MASK) == APL_FRAME_TYPE_KVP)
                            {
                                frameHeader &= APL_FRAME_COUNT_MASK;
                                for (transaction=0; transaction<frameHeader; transaction++)
                                {
                                    sequenceNumber          = APLGet();
                                    command                 = APLGet();
                                    attributeId.byte.LSB    = APLGet();
                                    attributeId.byte.MSB    = APLGet();

                                    //dataType = command & APL_FRAME_DATA_TYPE_MASK;
                                    command &= APL_FRAME_COMMAND_MASK;

                                    if ((params.APSDE_DATA_indication.ClusterId == OnOffSRC_CLUSTER) &&
                                        (attributeId.Val == OnOffSRC_OnOff))
                                    {
                                        if ((command == APL_FRAME_COMMAND_SET) ||
                                            (command == APL_FRAME_COMMAND_SETACK))
                                        {
                                            // Prepare a response in case it is needed.
                                            TxBuffer[TxData++] = APL_FRAME_TYPE_KVP | 1;    // KVP, 1 transaction
                                            TxBuffer[TxData++] = sequenceNumber;
                                            TxBuffer[TxData++] = APL_FRAME_COMMAND_SET_RES | (APL_FRAME_DATA_TYPE_UINT8 << 4);
                                            TxBuffer[TxData++] = attributeId.byte.LSB;
                                            TxBuffer[TxData++] = attributeId.byte.MSB;

                                            // Data type for this attibute must be APL_FRAME_DATA_TYPE_UINT8
                                            data = APLGet();
                                            switch (data)
                                            {
                                                
												case PUERTA_INFO:
                                                    ConsolePutROMString( (ROM char *)" Informando sobre el estado de la puerta.\r\n" );
													if(MESSAGE_PUERTA==1){
														ConsolePutROMString((ROM char *)"La puerta esta abierta" );
	                                                    TxBuffer[TxData++] = PUERTA_ABIERTA;
													}
													if(MESSAGE_PUERTA==0){
														ConsolePutROMString((ROM char *)"La puerta esta cerrada" );
	                                                    TxBuffer[TxData++] = PUERTA_CERRADA;
													}
                                                    break;
                                                default:
                                                    PrintChar( data );
                                                    ConsolePutROMString( (ROM char *)" Mensaje de puerta invalido.\r\n" );
                                                    TxBuffer[TxData++] = KVP_INVALID_ATTRIBUTE_DATA;
                                                    break;
                                            }
                                        }
										//Si lo ponemos nunca entra y no transmite los datos
                                        //if (command == APL_FRAME_COMMAND_SETACK)
                                        //{
                                            // Send back an application level acknowledge.
                                            ZigBeeBlockTx();

                                            // Take care here that parameters are not overwritten before they are used.
                                            // We can use the data byte as a temporary variable.
                                            params.APSDE_DATA_request.DstAddrMode = params.APSDE_DATA_indication.SrcAddrMode;
                                            params.APSDE_DATA_request.DstEndpoint = params.APSDE_DATA_indication.SrcEndpoint;
                                            params.APSDE_DATA_request.DstAddress.ShortAddr = params.APSDE_DATA_indication.SrcAddress.ShortAddr;

                                            //params.APSDE_DATA_request.asduLength; TxData
                                            //params.APSDE_DATA_request.ProfileId; unchanged
                                            params.APSDE_DATA_request.RadiusCounter = DEFAULT_RADIUS;
                                            params.APSDE_DATA_request.DiscoverRoute = ROUTE_DISCOVERY_ENABLE;
											#ifdef I_SUPPORT_SECURITY
												params.APSDE_DATA_request.TxOptions.Val = 1;
											#else											                                            
                                            	params.APSDE_DATA_request.TxOptions.Val = 0;
											#endif                                            
                                            params.APSDE_DATA_request.SrcEndpoint = EP_PUERTA;
                                            //params.APSDE_DATA_request.ClusterId; unchanged

                                            currentPrimitive = APSDE_DATA_request;
                                       
                                    }
                                    // TODO read to the end of the transaction.
                                } // each transaction
                            } // frame type
                            break;
							
						case EP_MOVIMIENTO:
                            if ((frameHeader & APL_FRAME_TYPE_MASK) == APL_FRAME_TYPE_KVP)
                            {
                                frameHeader &= APL_FRAME_COUNT_MASK;
                                for (transaction=0; transaction<frameHeader; transaction++)
                                {
                                    sequenceNumber          = APLGet();
                                    command                 = APLGet();
                                    attributeId.byte.LSB    = APLGet();
                                    attributeId.byte.MSB    = APLGet();

                                    //dataType = command & APL_FRAME_DATA_TYPE_MASK;
                                    command &= APL_FRAME_COMMAND_MASK;

                                    if ((params.APSDE_DATA_indication.ClusterId == OnOffSRC_CLUSTER) &&
                                        (attributeId.Val == OnOffSRC_OnOff))
                                    {
                                        if ((command == APL_FRAME_COMMAND_SET) ||
                                            (command == APL_FRAME_COMMAND_SETACK))
                                        {
                                            // Prepare a response in case it is needed.
                                            TxBuffer[TxData++] = APL_FRAME_TYPE_KVP | 1;    // KVP, 1 transaction
                                            TxBuffer[TxData++] = sequenceNumber;
                                            TxBuffer[TxData++] = APL_FRAME_COMMAND_SET_RES | (APL_FRAME_DATA_TYPE_UINT8 << 4);
                                            TxBuffer[TxData++] = attributeId.byte.LSB;
                                            TxBuffer[TxData++] = attributeId.byte.MSB;

                                            // Data type for this attibute must be APL_FRAME_DATA_TYPE_UINT8
                                            data = APLGet();
                                            switch (data)
                                            {
                                                
												case MOVIMIENTO_INFO:
                                                    ConsolePutROMString( (ROM char *)" Informando presencia.\r\n" );
													if(MESSAGE_MOVIMIENTO==1){
														ConsolePutROMString((ROM char *)"No hay movimiento (nadie)" );
	                                                    TxBuffer[TxData++] = MOVIMIENTO_OFF;
													}
													if(MESSAGE_MOVIMIENTO==0){
														ConsolePutROMString((ROM char *)"Hay movimiento (alguien)" );
	                                                    TxBuffer[TxData++] = MOVIMIENTO_ON;
													}
                                                    break;
                                                default:
                                                    PrintChar( data );
                                                    ConsolePutROMString( (ROM char *)" Mensaje de presencia invalido.\r\n" );
                                                    TxBuffer[TxData++] = KVP_INVALID_ATTRIBUTE_DATA;
                                                    break;
                                            }
                                        }
										//Si lo ponemos nunca entra y no transmite los datos
                                        //if (command == APL_FRAME_COMMAND_SETACK)
                                        //{
                                            // Send back an application level acknowledge.
                                            ZigBeeBlockTx();

                                            // Take care here that parameters are not overwritten before they are used.
                                            // We can use the data byte as a temporary variable.
                                            params.APSDE_DATA_request.DstAddrMode = params.APSDE_DATA_indication.SrcAddrMode;
                                            params.APSDE_DATA_request.DstEndpoint = params.APSDE_DATA_indication.SrcEndpoint;
                                            params.APSDE_DATA_request.DstAddress.ShortAddr = params.APSDE_DATA_indication.SrcAddress.ShortAddr;

                                            //params.APSDE_DATA_request.asduLength; TxData
                                            //params.APSDE_DATA_request.ProfileId; unchanged
                                            params.APSDE_DATA_request.RadiusCounter = DEFAULT_RADIUS;
                                            params.APSDE_DATA_request.DiscoverRoute = ROUTE_DISCOVERY_ENABLE;
											#ifdef I_SUPPORT_SECURITY
												params.APSDE_DATA_request.TxOptions.Val = 1;
											#else											                                            
                                            	params.APSDE_DATA_request.TxOptions.Val = 0;
											#endif                                            
                                            params.APSDE_DATA_request.SrcEndpoint = EP_MOVIMIENTO;
                                            //params.APSDE_DATA_request.ClusterId; unchanged

                                            currentPrimitive = APSDE_DATA_request;
                                       
                                    }
                                    // TODO read to the end of the transaction.
                                } // each transaction
                            } // frame type
                            break;			
							
							case EP_PERSIANA:
                            if ((frameHeader & APL_FRAME_TYPE_MASK) == APL_FRAME_TYPE_KVP)
                            {
                                frameHeader &= APL_FRAME_COUNT_MASK;
                                for (transaction=0; transaction<frameHeader; transaction++)
                                {
                                    sequenceNumber          = APLGet();
                                    command                 = APLGet();
                                    attributeId.byte.LSB    = APLGet();
                                    attributeId.byte.MSB    = APLGet();

                                    //dataType = command & APL_FRAME_DATA_TYPE_MASK;
                                    command &= APL_FRAME_COMMAND_MASK;

                                    if ((params.APSDE_DATA_indication.ClusterId == OnOffSRC_CLUSTER) &&
                                        (attributeId.Val == OnOffSRC_OnOff))
                                    {
                                        if ((command == APL_FRAME_COMMAND_SET) ||
                                            (command == APL_FRAME_COMMAND_SETACK))
                                        {
                                            // Prepare a response in case it is needed.
                                            TxBuffer[TxData++] = APL_FRAME_TYPE_KVP | 1;    // KVP, 1 transaction
                                            TxBuffer[TxData++] = sequenceNumber;
                                            TxBuffer[TxData++] = APL_FRAME_COMMAND_SET_RES | (APL_FRAME_DATA_TYPE_UINT8 << 4);
                                            TxBuffer[TxData++] = attributeId.byte.LSB;
                                            TxBuffer[TxData++] = attributeId.byte.MSB;

                                            // Data type for this attibute must be APL_FRAME_DATA_TYPE_UINT8
                                            data = APLGet();
                                            switch (data)
                                            {
                                                case PERSIANA_ABIERTA:
                                                    ConsolePutROMString( (ROM char *)"Abrir persiana.\r\n" );
													for (i=0; i<=10 ; i++)
													{	
														//Corresponde a la R
														_asm
															RRNCF ROTAR,1,1
														_endasm
														PORTD=ROTAR;
														PrintChar(ROTAR);
														Delay10KTCYx(50);
													}
													
                                                    ESTADO_PERSIANA = 1;
                                                    TxBuffer[TxData++] = SUCCESS;
                                                    break;
                                                case PERSIANA_CERRADA:
                                                    ConsolePutROMString( (ROM char *)"Cerrar persiana.\r\n" );
													for (i=0; i<=10 ; i++)
													{
														//Corresponde a la L
														_asm
															RLNCF ROTAR,1,1
														_endasm
														PORTD=ROTAR;
														PrintChar(ROTAR);
														Delay10KTCYx(50);
													}
                                                    ESTADO_PERSIANA = 0;
                                                    TxBuffer[TxData++] = SUCCESS;
                                                    break;
												case PERSIANA_CAMBIO:
                                                    ConsolePutROMString( (ROM char *)"Cambiando persiana.\r\n" );
													if(ESTADO_PERSIANA==1){
														ConsolePutROMString((ROM char *)"Cerrando persiana" );
	                                                    TxBuffer[TxData++] = SUCCESS;
														for (i=0; i<=10 ; i++)
														{
															//Corresponde a la L
															_asm
																RLNCF ROTAR,1,1
															_endasm
															PORTD=ROTAR;
															PrintChar(ROTAR);
															Delay10KTCYx(50);
														}
														ESTADO_PERSIANA = 0;
													}
													else{
														ConsolePutROMString((ROM char *)"Abriendo persiana" );
	                                                    TxBuffer[TxData++] = SUCCESS;
														for (i=0; i<=10 ; i++)
														{	
															//Corresponde a la R
															_asm
																RRNCF ROTAR,1,1
															_endasm
															PORTD=ROTAR;
															PrintChar(ROTAR);
															Delay10KTCYx(50);
														}
														ESTADO_PERSIANA = 1;
													}
                                                    break;
												case PERSIANA_INFO:
                                                    ConsolePutROMString( (ROM char *)" Informando sobre el estado de la persiana.\r\n" );
													if(ESTADO_PERSIANA==1){
														ConsolePutROMString((ROM char *)"La persiana esta abierta" );
	                                                    TxBuffer[TxData++] = PERSIANA_ABIERTA;
													}
													if(ESTADO_PERSIANA==0){
														ConsolePutROMString((ROM char *)"La persiana esta cerrada" );
	                                                    TxBuffer[TxData++] = PERSIANA_CERRADA;
													}
                                                    break;
                                                default:
                                                    PrintChar( data );
                                                    ConsolePutROMString( (ROM char *)" Invalid persiana message.\r\n" );
                                                    TxBuffer[TxData++] = KVP_INVALID_ATTRIBUTE_DATA;
                                                    break;
                                            }
                                        }
										//Si lo ponemos nunca entra y no transmite los datos
                                        //if (command == APL_FRAME_COMMAND_SETACK)
                                        //{
                                            // Send back an application level acknowledge.
                                            ZigBeeBlockTx();

                                            // Take care here that parameters are not overwritten before they are used.
                                            // We can use the data byte as a temporary variable.
                                            params.APSDE_DATA_request.DstAddrMode = params.APSDE_DATA_indication.SrcAddrMode;
                                            params.APSDE_DATA_request.DstEndpoint = params.APSDE_DATA_indication.SrcEndpoint;
                                            params.APSDE_DATA_request.DstAddress.ShortAddr = params.APSDE_DATA_indication.SrcAddress.ShortAddr;

                                            //params.APSDE_DATA_request.asduLength; TxData
                                            //params.APSDE_DATA_request.ProfileId; unchanged
                                            params.APSDE_DATA_request.RadiusCounter = DEFAULT_RADIUS;
                                            params.APSDE_DATA_request.DiscoverRoute = ROUTE_DISCOVERY_ENABLE;
											#ifdef I_SUPPORT_SECURITY
												params.APSDE_DATA_request.TxOptions.Val = 1;
											#else											                                            
                                            	params.APSDE_DATA_request.TxOptions.Val = 0;
											#endif                                            
                                            params.APSDE_DATA_request.SrcEndpoint = EP_PERSIANA;
                                            //params.APSDE_DATA_request.ClusterId; unchanged

                                            currentPrimitive = APSDE_DATA_request;
                                    
                                    }
                                    // TODO read to the end of the transaction.
                                } // each transaction
                            } // frame type
                            break;
					
						case EP_ANALOGO:
                            if ((frameHeader & APL_FRAME_TYPE_MASK) == APL_FRAME_TYPE_KVP)
                            {
                                frameHeader &= APL_FRAME_COUNT_MASK;
                                for (transaction=0; transaction<frameHeader; transaction++)
                                {
                                    sequenceNumber          = APLGet();
                                    command                 = APLGet();
                                    attributeId.byte.LSB    = APLGet();
                                    attributeId.byte.MSB    = APLGet();

                                    //dataType = command & APL_FRAME_DATA_TYPE_MASK;
                                    command &= APL_FRAME_COMMAND_MASK;

                                    if ((params.APSDE_DATA_indication.ClusterId == OnOffSRC_CLUSTER) &&
                                        (attributeId.Val == OnOffSRC_OnOff))
                                    {
                                        if ((command == APL_FRAME_COMMAND_SET) ||
                                            (command == APL_FRAME_COMMAND_SETACK))
                                        {
                                            // Prepare a response in case it is needed.
                                            TxBuffer[TxData++] = APL_FRAME_TYPE_KVP | 1;    // KVP, 1 transaction
                                            TxBuffer[TxData++] = sequenceNumber;
                                            TxBuffer[TxData++] = APL_FRAME_COMMAND_SET_RES | (APL_FRAME_DATA_TYPE_UINT8 << 4);
                                            TxBuffer[TxData++] = attributeId.byte.LSB;
                                            TxBuffer[TxData++] = attributeId.byte.MSB;

                                            // Data type for this attibute must be APL_FRAME_DATA_TYPE_UINT8
                                            data = APLGet();
                                            switch (data)
                                            {
                                                case ANALOGO_INFO:
                                                    ConsolePutROMString( (ROM char *)" Informando sobre el ep analogo.\r\n" );
													
													OpenADC( ADC_FOSC_64 & 
													ADC_RIGHT_JUST & 
													ADC_12_TAD, 
													ADC_CH3 & 
													ADC_VREFPLUS_VDD & 
													ADC_VREFMINUS_VSS & 
													ADC_INT_OFF, 15 ); 
													
													Delay10TCYx(50); 
												
													ConvertADC(); 
														while( BusyADC() ); 
													DataADC = ReadADC(); 
													CloseADC(); 
													ADC_VAL=DataADC>>2;  //ROTACIÓN DOS BITS A LA IZQUIERDA
										 			ConsolePutROMString( (ROM char *)"/" );
													PrintChar(ADC_VAL);

													if(ADC_VAL>=0x00 && ADC_VAL<0x40)
														rango=1;
													if(ADC_VAL>=0x40 && ADC_VAL<0x80)
														rango=2;
													if(ADC_VAL>=0x80 && ADC_VAL<0xC0)
														rango=3;
													if(ADC_VAL>=0xC0 && ADC_VAL<=0xFF)
														rango=4;
													
	                                                TxBuffer[TxData++] = rango;
													
                                                    break;
                                                default:
                                                    PrintChar( data );
                                                    ConsolePutROMString( (ROM char *)" Invalid light message.\r\n" );
                                                    TxBuffer[TxData++] = KVP_INVALID_ATTRIBUTE_DATA;
                                                    break;
                                            }
                                        }
										//Si lo ponemos nunca entra y no transmite los datos
                                        //if (command == APL_FRAME_COMMAND_SETACK)
                                        //{
                                            // Send back an application level acknowledge.
                                            ZigBeeBlockTx();

                                            // Take care here that parameters are not overwritten before they are used.
                                            // We can use the data byte as a temporary variable.
                                            params.APSDE_DATA_request.DstAddrMode = params.APSDE_DATA_indication.SrcAddrMode;
                                            params.APSDE_DATA_request.DstEndpoint = params.APSDE_DATA_indication.SrcEndpoint;
                                            params.APSDE_DATA_request.DstAddress.ShortAddr = params.APSDE_DATA_indication.SrcAddress.ShortAddr;

                                            //params.APSDE_DATA_request.asduLength; TxData
                                            //params.APSDE_DATA_request.ProfileId; unchanged
                                            params.APSDE_DATA_request.RadiusCounter = DEFAULT_RADIUS;
                                            params.APSDE_DATA_request.DiscoverRoute = ROUTE_DISCOVERY_ENABLE;
											#ifdef I_SUPPORT_SECURITY
												params.APSDE_DATA_request.TxOptions.Val = 1;
											#else											                                            
                                            	params.APSDE_DATA_request.TxOptions.Val = 0;
											#endif                                            
                                            params.APSDE_DATA_request.SrcEndpoint = EP_ANALOGO;
                                            //params.APSDE_DATA_request.ClusterId; unchanged

                                            currentPrimitive = APSDE_DATA_request;
                                    
                                    }
                                    // TODO read to the end of the transaction.
                                } // each transaction
                            } // frame type
                            break;
////////////FIN END POINTS PERSONALIZADOS
                          
                        default:               
                			break;
                	}

                    APLDiscardRx();
                }
                break;

            case APSDE_DATA_confirm:
                if (params.APSDE_DATA_confirm.Status)
                {
                    ConsolePutROMString( (ROM char *)"Error " );
                    PrintChar( params.APSDE_DATA_confirm.Status );
                    ConsolePutROMString( (ROM char *)" sending message.\r\n" );
                }
                else
                {
                    ConsolePutROMString( (ROM char *)" Message sent successfully.\r\n" );
                }
                currentPrimitive = NO_PRIMITIVE;
                break;

            case NO_PRIMITIVE:
                if (!ZigBeeStatus.flags.bits.bNetworkJoined)
                {
                    if (!ZigBeeStatus.flags.bits.bTryingToJoinNetwork)
                    {
                        if (ZigBeeStatus.flags.bits.bTryOrphanJoin)
                        {
                            ConsolePutROMString( (ROM char *)"Trying to join network as an orphan...\r\n" );
                            params.NLME_JOIN_request.JoinAsRouter           = FALSE;
                            params.NLME_JOIN_request.RejoinNetwork          = TRUE;
                            params.NLME_JOIN_request.PowerSource            = NOT_MAINS_POWERED;
                            params.NLME_JOIN_request.RxOnWhenIdle           = FALSE;
                            params.NLME_JOIN_request.MACSecurity            = FALSE;
                            params.NLME_JOIN_request.ScanDuration           = 8;
                            params.NLME_JOIN_request.ScanChannels.Val       = ALLOWED_CHANNELS;
                            currentPrimitive = NLME_JOIN_request;
                        }
                        else
                        {
                            ConsolePutROMString( (ROM char *)"Trying to join network as a new device...\r\n" );
                            params.NLME_NETWORK_DISCOVERY_request.ScanDuration          = 6;
                            params.NLME_NETWORK_DISCOVERY_request.ScanChannels.Val      = ALLOWED_CHANNELS;
                            currentPrimitive = NLME_NETWORK_DISCOVERY_request;
                        }
                    }
                }
                else
                {
                    // See if I can do my own internal tasks.  We don't want to try to send a message
                    // if we just asked for one.
                    if (ZigBeeStatus.flags.bits.bDataRequestComplete && ZigBeeReady())
                    {

                        // ************************************************************************
                        // Place all processes that can send messages here.  Be sure to call
                        // ZigBeeBlockTx() when currentPrimitive is set to APSDE_DATA_request.
                        // ************************************************************************
						
							OpenADC( ADC_FOSC_64 & 
													ADC_RIGHT_JUST & 
													ADC_12_TAD, 
													ADC_CH3 & 
													ADC_VREFPLUS_VDD & 
													ADC_VREFMINUS_VSS & 
													ADC_INT_OFF, 15 ); 
													
													Delay10TCYx(50); 
												
													ConvertADC(); 
														while( BusyADC() ); 
													DataADC = ReadADC(); 
													CloseADC(); 
													ADC_VAL=DataADC>>2;  //ROTACIÓN DOS BITS A LA IZQUIERDA

													if(ADC_VAL>=0x00 && ADC_VAL<0x40)
														rango=1;
													if(ADC_VAL>=0x40 && ADC_VAL<0x80)
														rango=2;
													if(ADC_VAL>=0x80 && ADC_VAL<0xC0)
														rango=3;
													if(ADC_VAL>=0xC0 && ADC_VAL<=0xFF)
														rango=4;
													
/*
							if ( myStatusFlags.bits.bLightSwitchToggled )
				            {
								ConsolePutROMString((ROM char *)"if para prender bombillo \r\n" );
								MESSAGE_INDICATION ^= 1;
								myStatusFlags.bits.bLightSwitchToggled = FALSE;
								INTCONbits.RBIE = 1;
							}
											
								if(BANDERA_PERSIANA!=ESTADO_PERSIANA)
									{
										
										BANDERA_PERSIANA=ESTADO_PERSIANA;
										ZigBeeBlockTx();
										
			                            TxBuffer[TxData++] = APL_FRAME_TYPE_KVP | 1;    // KVP, 1 transaction
			                            TxBuffer[TxData++] = APLGetTransId();
			                            TxBuffer[TxData++] = APL_FRAME_COMMAND_SET | (APL_FRAME_DATA_TYPE_UINT8 << 4);
			                            TxBuffer[TxData++] = OnOffSRC_OnOff & 0xFF;         // Attribute ID LSB
			                            TxBuffer[TxData++] = (OnOffSRC_OnOff >> 8) & 0xFF;  // Attribute ID MSB
			                            
										if(ESTADO_PERSIANA==1){
											ConsolePutROMString((ROM char *)"La persiana esta abierta \r\n" );
	                                      	TxBuffer[TxData++] = PERSIANA_ABIERTA;
										}
										if(ESTADO_PERSIANA==0){
											ConsolePutROMString((ROM char *)"La persiana esta cerrada \r\n" );
	                                        TxBuffer[TxData++] = PERSIANA_CERRADA;
										}
			
			                            #ifdef USE_BINDINGS
			                                params.APSDE_DATA_request.DstAddrMode = APS_ADDRESS_NOT_PRESENT;
			                            #else
			                                params.APSDE_DATA_request.DstAddrMode = APS_ADDRESS_16_BIT;
			                                params.APSDE_DATA_request.DstEndpoint = EP_PERSIANA;
			                                params.APSDE_DATA_request.DstAddress.ShortAddr = destinationAddress;
			                            #endif
			
			                            //params.APSDE_DATA_request.asduLength; TxData
			                            params.APSDE_DATA_request.ProfileId.Val = MY_PROFILE_ID;
			                            params.APSDE_DATA_request.RadiusCounter = DEFAULT_RADIUS;
			                            params.APSDE_DATA_request.DiscoverRoute = ROUTE_DISCOVERY_ENABLE;
										#ifdef I_SUPPORT_SECURITY
											params.APSDE_DATA_request.TxOptions.Val = 1;
										#else                            
			                            	params.APSDE_DATA_request.TxOptions.Val = 0;
										#endif                            
			                            params.APSDE_DATA_request.SrcEndpoint = EP_PERSIANA;
			                            params.APSDE_DATA_request.ClusterId = OnOffSRC_CLUSTER;
										
			                            currentPrimitive = APSDE_DATA_request;
										break;
									}
									else//else5
									{
								*/

									if(BANDERA_ANALOGO!=rango)
									{
										BANDERA_ANALOGO=rango;
										ZigBeeBlockTx();
										
			                            TxBuffer[TxData++] = APL_FRAME_TYPE_KVP | 1;    // KVP, 1 transaction
			                            TxBuffer[TxData++] = APLGetTransId();
			                            TxBuffer[TxData++] = APL_FRAME_COMMAND_SET | (APL_FRAME_DATA_TYPE_UINT8 << 4);
			                            TxBuffer[TxData++] = OnOffSRC_OnOff & 0xFF;         // Attribute ID LSB
			                            TxBuffer[TxData++] = (OnOffSRC_OnOff >> 8) & 0xFF;  // Attribute ID MSB
			                            TxBuffer[TxData++] = rango;

										ConsolePutROMString((ROM char *)"valor en análogo: " );
										PrintChar(ADC_VAL);
										ConsolePutROMString((ROM char *)"rango: " );								
										PrintChar(rango);
										ConsolePutROMString((ROM char *)"\r\n");

			                            #ifdef USE_BINDINGS
			                                params.APSDE_DATA_request.DstAddrMode = APS_ADDRESS_NOT_PRESENT;
			                            #else
			                                params.APSDE_DATA_request.DstAddrMode = APS_ADDRESS_16_BIT;
			                                params.APSDE_DATA_request.DstEndpoint = EP_ANALOGO;
			                                params.APSDE_DATA_request.DstAddress.ShortAddr = destinationAddress;
			                            #endif
			
			                            //params.APSDE_DATA_request.asduLength; TxData
			                            params.APSDE_DATA_request.ProfileId.Val = MY_PROFILE_ID;
			                            params.APSDE_DATA_request.RadiusCounter = DEFAULT_RADIUS;
			                            params.APSDE_DATA_request.DiscoverRoute = ROUTE_DISCOVERY_ENABLE;
										#ifdef I_SUPPORT_SECURITY
											params.APSDE_DATA_request.TxOptions.Val = 1;
										#else                            
			                            	params.APSDE_DATA_request.TxOptions.Val = 0;
										#endif                            
			                            params.APSDE_DATA_request.SrcEndpoint = EP_ANALOGO;
			                            params.APSDE_DATA_request.ClusterId = OnOffSRC_CLUSTER;

			                            currentPrimitive = APSDE_DATA_request;
										break;
									}
									else//else1
									{
/*
									if(BANDERA_BOMBILLO!=MESSAGE_INDICATION)
									{
										BANDERA_BOMBILLO=MESSAGE_INDICATION;

										ZigBeeBlockTx();
										
			                            TxBuffer[TxData++] = APL_FRAME_TYPE_KVP | 1;    // KVP, 1 transaction
			                            TxBuffer[TxData++] = APLGetTransId();
			                            TxBuffer[TxData++] = APL_FRAME_COMMAND_SET | (APL_FRAME_DATA_TYPE_UINT8 << 4);
			                            TxBuffer[TxData++] = OnOffSRC_OnOff & 0xFF;         // Attribute ID LSB
			                            TxBuffer[TxData++] = (OnOffSRC_OnOff >> 8) & 0xFF;  // Attribute ID MSB
			                            
										if(MESSAGE_INDICATION==1){
											ConsolePutROMString((ROM char *)"La luz esta prendida \r\n" );
				                            TxBuffer[TxData++] = LIGHT_ON;
										}
										if(MESSAGE_INDICATION==0){
											ConsolePutROMString((ROM char *)"La luz esta apagada \r\n" );
				                            TxBuffer[TxData++] = LIGHT_OFF;
										}
			
			                            #ifdef USE_BINDINGS
			                                params.APSDE_DATA_request.DstAddrMode = APS_ADDRESS_NOT_PRESENT;
			                            #else
			                                params.APSDE_DATA_request.DstAddrMode = APS_ADDRESS_16_BIT;
			                                params.APSDE_DATA_request.DstEndpoint = EP_LIGHT;
			                                params.APSDE_DATA_request.DstAddress.ShortAddr = destinationAddress;
			                            #endif
			
			                            //params.APSDE_DATA_request.asduLength; TxData
			                            params.APSDE_DATA_request.ProfileId.Val = MY_PROFILE_ID;
			                            params.APSDE_DATA_request.RadiusCounter = DEFAULT_RADIUS;
			                            params.APSDE_DATA_request.DiscoverRoute = ROUTE_DISCOVERY_ENABLE;
										#ifdef I_SUPPORT_SECURITY
											params.APSDE_DATA_request.TxOptions.Val = 1;
										#else                            
			                            	params.APSDE_DATA_request.TxOptions.Val = 0;
										#endif                            
			                            params.APSDE_DATA_request.SrcEndpoint = EP_LIGHT;
			                            params.APSDE_DATA_request.ClusterId = OnOffSRC_CLUSTER;

			                            currentPrimitive = APSDE_DATA_request;
										break;
									}
									else//else 2
									{


		                       		if(BANDERA_PUERTA!=MESSAGE_PUERTA)
									{
										BANDERA_PUERTA=MESSAGE_PUERTA;

										ZigBeeBlockTx();
										
			                            TxBuffer[TxData++] = APL_FRAME_TYPE_KVP | 1;    // KVP, 1 transaction
			                            TxBuffer[TxData++] = APLGetTransId();
			                            TxBuffer[TxData++] = APL_FRAME_COMMAND_SET | (APL_FRAME_DATA_TYPE_UINT8 << 4);
			                            TxBuffer[TxData++] = OnOffSRC_OnOff & 0xFF;         // Attribute ID LSB
			                            TxBuffer[TxData++] = (OnOffSRC_OnOff >> 8) & 0xFF;  // Attribute ID MSB
			                            
			
										if(MESSAGE_PUERTA==1){
											ConsolePutROMString((ROM char *)"La puerta esta abierta \r\n" );
					                        TxBuffer[TxData++] = PUERTA_ABIERTA;
										}
										if(MESSAGE_PUERTA==0){
											ConsolePutROMString((ROM char *)"La puerta esta cerrada \r\n" );
					                        TxBuffer[TxData++] = PUERTA_CERRADA;
										}
			
			                            #ifdef USE_BINDINGS
			                                params.APSDE_DATA_request.DstAddrMode = APS_ADDRESS_NOT_PRESENT;
			                            #else
			                                params.APSDE_DATA_request.DstAddrMode = APS_ADDRESS_16_BIT;
			                                params.APSDE_DATA_request.DstEndpoint = EP_PUERTA;
			                                params.APSDE_DATA_request.DstAddress.ShortAddr = destinationAddress;
			                            #endif
			
			                            //params.APSDE_DATA_request.asduLength; TxData
			                            params.APSDE_DATA_request.ProfileId.Val = MY_PROFILE_ID;
			                            params.APSDE_DATA_request.RadiusCounter = DEFAULT_RADIUS;
			                            params.APSDE_DATA_request.DiscoverRoute = ROUTE_DISCOVERY_ENABLE;
										#ifdef I_SUPPORT_SECURITY
											params.APSDE_DATA_request.TxOptions.Val = 1;
										#else                            
			                            	params.APSDE_DATA_request.TxOptions.Val = 0;
										#endif                            
			                            params.APSDE_DATA_request.SrcEndpoint = EP_PUERTA;
			                            params.APSDE_DATA_request.ClusterId = OnOffSRC_CLUSTER;
										
			                            currentPrimitive = APSDE_DATA_request;
										break;
									}
									else//else 3
									{/*
									if(BANDERA_PRESENCIA!=MESSAGE_MOVIMIENTO)
									{
										BANDERA_PRESENCIA=MESSAGE_MOVIMIENTO;
										ZigBeeBlockTx();
										
			                            TxBuffer[TxData++] = APL_FRAME_TYPE_KVP | 1;    // KVP, 1 transaction
			                            TxBuffer[TxData++] = APLGetTransId();
			                            TxBuffer[TxData++] = APL_FRAME_COMMAND_SET | (APL_FRAME_DATA_TYPE_UINT8 << 4);
			                            TxBuffer[TxData++] = OnOffSRC_OnOff & 0xFF;         // Attribute ID LSB
			                            TxBuffer[TxData++] = (OnOffSRC_OnOff >> 8) & 0xFF;  // Attribute ID MSB
			                            
										if(MESSAGE_MOVIMIENTO==1){
											ConsolePutROMString((ROM char *)"No hay movimiento (nadie) \r\n" );
				                            TxBuffer[TxData++] = MOVIMIENTO_OFF;
										}
										if(MESSAGE_MOVIMIENTO==0){
											ConsolePutROMString((ROM char *)"Hay movimiento (alguien) \r\n" );
				                            TxBuffer[TxData++] = MOVIMIENTO_ON;
										}
			
			                            #ifdef USE_BINDINGS
			                                params.APSDE_DATA_request.DstAddrMode = APS_ADDRESS_NOT_PRESENT;
			                            #else
			                                params.APSDE_DATA_request.DstAddrMode = APS_ADDRESS_16_BIT;
			                                params.APSDE_DATA_request.DstEndpoint = EP_MOVIMIENTO;
			                                params.APSDE_DATA_request.DstAddress.ShortAddr = destinationAddress;
			                            #endif
			
			                            //params.APSDE_DATA_request.asduLength; TxData
			                            params.APSDE_DATA_request.ProfileId.Val = MY_PROFILE_ID;
			                            params.APSDE_DATA_request.RadiusCounter = DEFAULT_RADIUS;
			                            params.APSDE_DATA_request.DiscoverRoute = ROUTE_DISCOVERY_ENABLE;
										#ifdef I_SUPPORT_SECURITY
											params.APSDE_DATA_request.TxOptions.Val = 1;
										#else                            
			                            	params.APSDE_DATA_request.TxOptions.Val = 0;
										#endif                            
			                            params.APSDE_DATA_request.SrcEndpoint = EP_MOVIMIENTO;
			                            params.APSDE_DATA_request.ClusterId = OnOffSRC_CLUSTER;

			                            currentPrimitive = APSDE_DATA_request;
										break;
									}
									else//else 4
									{
									*/
				                        #ifdef USE_BINDINGS
				                            if (myStatusFlags.bits.bBroadcastSwitchToggled)
				                            {
				                                // Send END_DEVICE_BIND_req
				
				                                myStatusFlags.bits.bBroadcastSwitchToggled = FALSE;
				                                ZigBeeBlockTx();
				
				                                TxBuffer[TxData++] = APL_FRAME_TYPE_MSG | 1;    // KVP, 1 transaction
				                                TxBuffer[TxData++] = APLGetTransId();
				                                #if defined(I_AM_LIGHT) && defined(I_AM_SWITCH)
				                                    TxBuffer[TxData++] = 9; // Transaction Length
				                                #else
				                                    TxBuffer[TxData++] = 8; // Transaction Length
				                                #endif
				
				                                // Binding Target
				                                TxBuffer[TxData++] = 0x00;      // Binding Target
				                                TxBuffer[TxData++] = 0x00;
				                                TxBuffer[TxData++] = EP_LIGHT;
				                                TxBuffer[TxData++] = MY_PROFILE_ID_LSB;
				                                TxBuffer[TxData++] = MY_PROFILE_ID_MSB;
				
				                                #ifdef I_AM_LIGHT
				                                    TxBuffer[TxData++] = 1;     // Input clusters
				                                    TxBuffer[TxData++] = OnOffSRC_CLUSTER;
				                                #else
				                                    TxBuffer[TxData++] = 0;
				                                #endif
				
				                                #ifdef I_AM_SWITCH
				                                    TxBuffer[TxData++] = 1;     // Output clusters
				                                    TxBuffer[TxData++] = OnOffSRC_CLUSTER;
				                                #else
				                                    TxBuffer[TxData++] = 0;
				                                #endif
				
				                                params.APSDE_DATA_request.DstAddrMode = APS_ADDRESS_16_BIT;
				                                params.APSDE_DATA_request.DstEndpoint = EP_ZDO;
				                                params.APSDE_DATA_request.DstAddress.ShortAddr.Val = 0x0000;
				
				                                //params.APSDE_DATA_request.asduLength; TxData
				                                params.APSDE_DATA_request.ProfileId.Val = ZDO_PROFILE_ID;
				                                params.APSDE_DATA_request.RadiusCounter = DEFAULT_RADIUS;
				                                params.APSDE_DATA_request.DiscoverRoute = ROUTE_DISCOVERY_ENABLE;
												#ifdef I_SUPPORT_SECURITY
													params.APSDE_DATA_request.TxOptions.Val = 1;
												#else								                                
				                                	params.APSDE_DATA_request.TxOptions.Val = 0;
												#endif                                
				                                params.APSDE_DATA_request.SrcEndpoint = EP_ZDO;
				                                params.APSDE_DATA_request.ClusterId = END_DEVICE_BIND_req;
				
				                                ConsolePutROMString( (ROM char *)" Trying to send END_DEVICE_BIND_req.\r\n" );
				
				                                currentPrimitive = APSDE_DATA_request;
				                            }
				                        #else
				                            if (myStatusFlags.bits.bBroadcastSwitchToggled)
				                            {
				                                // Send NWK_ADDR_req
				
				                                myStatusFlags.bits.bBroadcastSwitchToggled = FALSE;
				                                ZigBeeBlockTx();
				
				                                TxBuffer[TxData++] = APL_FRAME_TYPE_MSG | 1;    // KVP, 1 transaction
				                                TxBuffer[TxData++] = APLGetTransId();
				                                TxBuffer[TxData++] = 10; // Transaction Length
				
				                                // IEEEAddr of the node we want to find.  !!!Must match our other PICDEM Z!!!
				                                TxBuffer[TxData++] = 0x54;
				                                TxBuffer[TxData++] = 0x00;
				                                TxBuffer[TxData++] = 0x00;
				                                TxBuffer[TxData++] = 0x00;
				                                TxBuffer[TxData++] = 0x00;
				                                TxBuffer[TxData++] = 0xa3;
				                                TxBuffer[TxData++] = 0x04;
				                                TxBuffer[TxData++] = 0x00;
				
				                                // RequestType
				                                TxBuffer[TxData++] = 0x00;
				
				                                // StartIndex
				                                TxBuffer[TxData++] = 0x00;
				
				                                params.APSDE_DATA_request.DstAddrMode = APS_ADDRESS_16_BIT;
				                                params.APSDE_DATA_request.DstEndpoint = EP_ZDO;
				                                params.APSDE_DATA_request.DstAddress.ShortAddr.Val = 0xFFFF;
				
				                                //params.APSDE_DATA_request.asduLength; TxData
				                                params.APSDE_DATA_request.ProfileId.Val = ZDO_PROFILE_ID;
				                                params.APSDE_DATA_request.RadiusCounter = DEFAULT_RADIUS;
				                                params.APSDE_DATA_request.DiscoverRoute = ROUTE_DISCOVERY_ENABLE;
												#ifdef I_SUPPORT_SECURITY
													params.APSDE_DATA_request.TxOptions.Val = 1;
												#else								                                
				                                	params.APSDE_DATA_request.TxOptions.Val = 0;
												#endif                                
				                                params.APSDE_DATA_request.SrcEndpoint = EP_ZDO;
				                                params.APSDE_DATA_request.ClusterId = NWK_ADDR_req;
				
				                                ConsolePutROMString( (ROM char *)" Trying to send NWK_ADDR_req.\r\n" );
				
				                                currentPrimitive = APSDE_DATA_request;
				                            }
				                        #endif
				
				                        // We've processed any key press, so re-enable interrupts.
				                        INTCONbits.RBIE = 1;

										} //cierra else 5
									//}//cierra else 4
							//	}//Cierra else 3
						//	}//CIERRA ELSE 2
						//}//CIERRA ELSE 1
                    }

                    // If we don't have to execute a primitive, see if we need to request data from
                    // our parent, or if we can go to sleep.
                    if (currentPrimitive == NO_PRIMITIVE)
                    {
                        if (!ZigBeeStatus.flags.bits.bDataRequestComplete)
                        {
                            // We have not received all data from our parent.  If we are not waiting
                            // for an answer from a data request, send a data request.
                            if (!ZigBeeStatus.flags.bits.bRequestingData)
                            {
                                if (ZigBeeReady())
                                {
                                    // Our parent still may have data for us.
                                    params.NLME_SYNC_request.Track = FALSE;
                                    currentPrimitive = NLME_SYNC_request;
                                    ConsolePutROMString( (ROM char *)"Requesting data...\r\n" );
                                }
                            }
                        }
                        else
                        {
                            if (!ZigBeeStatus.flags.bits.bHasBackgroundTasks && myProcessesAreDone())
                            {
                                // We do not have a primitive to execute, we've extracted all messages
                                // that our parent has for us, the stack has no background tasks,
                                // and all application-specific processes are complete.  Now we can
                                // go to sleep.  Make sure that the UART is finished, turn off the transceiver,
                                // and make sure that we wakeup from key press.
                                if(APLDisable() == TRUE)
                                {
	                                ConsolePutROMString( (ROM char *)"Going to sleep...\r\n" );
    	                            while (!ConsoleIsPutReady());
                                	APLDisable();
    	                            INTCONbits.RBIE = 1;
	                                SLEEP();
        	                        NOP();

	                                // We just woke up from sleep. Turn on the transceiver and
	                                // request data from our parent.
	                                APLEnable();
	                                params.NLME_SYNC_request.Track = FALSE;
	                                currentPrimitive = NLME_SYNC_request;
	                                ConsolePutROMString( (ROM char *)"Requesting data...\r\n" );
								}
                            }
                        }
                    }
                }
                break;

            default:
                PrintChar( currentPrimitive );
                ConsolePutROMString( (ROM char *)" Unhandled primitive.\r\n" );
                currentPrimitive = NO_PRIMITIVE;
        }

        // *********************************************************************
        // Place any non-ZigBee related processing here.  Be sure that the code
        // will loop back and execute ZigBeeTasks() in a timely manner.
        // *********************************************************************

    }
}

/*******************************************************************************
myProcessesAreDone

This routine should contain any tests that are required by the application to
confirm that it can go to sleep.  If the application can go to sleep, this
routine should return TRUE.  If the application is still busy, this routine
should return FALSE.
*******************************************************************************/

BOOL myProcessesAreDone( void )
{
    return (myStatusFlags.bits.bBroadcastSwitchToggled==FALSE) && (myStatusFlags.bits.bLightSwitchToggled==FALSE);
}

/*******************************************************************************
HardwareInit

All port directioning and SPI must be initialized before calling ZigBeeInit().

For demonstration purposes, required signals are configured individually.
*******************************************************************************/
void HardwareInit(void)
{

    //-------------------------------------------------------------------------
    // This section is required to initialize the PICDEM Z for the CC2420
    // and the ZigBee Stack.
    //-------------------------------------------------------------------------

    #ifdef USE_EXTERNAL_NVM
        EEPROM_nCS          = 1;
        EEPROM_nCS_TRIS     = 0;
    #endif

    #if defined(USE_EXTERNAL_NVM) && !defined(EE_AND_RF_SHARE_SPI)
        RF_SPIInit();
        EE_SPIInit();
    #else
        SPIInit();
    #endif

    #if (RF_CHIP == MRF24J40)
        // Start with MRF24J40 disabled and not selected
        PHY_CS              = 1;
        PHY_RESETn          = 1;

        // Set the directioning for the MRF24J40 pin connections.
        PHY_CS_TRIS         = 0;
        PHY_RESETn_TRIS     = 0;

        // Initialize the interrupt.
        INTCON2bits.INTEDG0 = 0;
    #elif (RF_CHIP==UZ2400)
        // Start with UZ2400 disabled and not selected
        PHY_SEN             = 1;
        PHY_RESETn          = 1;

        // Set the directioning for the UZ2400 pin connections.
        PHY_SEN_TRIS        = 0;
        PHY_RESETn_TRIS     = 0;

        // Initialize the interrupt.
        INTCON2bits.INTEDG0 = 0;
    #elif (RF_CHIP==CC2420)
        // CC2420 I/O assignments with respect to PIC:
        //NOTE: User must make sure that pin is capable of correct digital operation.
        //      This may require modificaiton of which pins are digital and analog.
        //NOTE: The stack requires that the SPI interface be located on LATC3 (SCK),
        //      RC4 (SO), and LATC5 (SI).
        //NOTE: The appropriate config bit must be set such that FIFOP is the CCP2
        //      input pin. The stack uses the CCP2 interrupt.

        // Start with CC2420 disabled and not selected
        PHY_CSn             = 1;
        PHY_VREG_EN         = 0;
        PHY_RESETn          = 1;

        // Set the directioning for the CC2420 pin connections.
        PHY_FIFO_TRIS       = 1;    // FIFO      (Input)
        PHY_SFD_TRIS        = 1;    // SFD       (Input - Generates interrupt on falling edge)
        PHY_FIFOP_TRIS      = 1;    // FIFOP     (Input - Used to detect overflow, CCP2 interrupt)
        PHY_CSn_TRIS        = 0;    // CSn       (Output - to select CC2420 SPI slave)
        PHY_VREG_EN_TRIS    = 0;    // VREG_EN   (Output - to enable CC2420 voltage regulator)
        PHY_RESETn_TRIS     = 0;    // RESETn    (Output - to reset CC2420)
    #else
        #error Unknown transceiver selected
    #endif

    #if defined(USE_EXTERNAL_NVM) && !defined(EE_AND_RF_SHARE_SPI)
        // Initialize the SPI1 pins and directions
        LATCbits.LATC3               = 0;    // SCK
        LATCbits.LATC5               = 1;    // SDO
        TRISCbits.TRISC3             = 0;    // SCK
        TRISCbits.TRISC4             = 1;    // SDI
        TRISCbits.TRISC5             = 0;    // SDO
    
        // Initialize the SPI2 pins and directions
        LATDbits.LATD6               = 0;    // SCK
        LATDbits.LATD4               = 1;    // SDO
        TRISDbits.TRISD6             = 0;    // SCK
        TRISDbits.TRISD5             = 1;    // SDI
        TRISDbits.TRISD4             = 0;    // SDO
    
        RF_SSPSTAT_REG = 0x40;
        RF_SSPCON1_REG = 0x21;
        EE_SSPSTAT_REG = 0x40;
        EE_SSPCON1_REG = 0x21;
    #else
        // Initialize the SPI pins and directions
        LATCbits.LATC3               = 0;    // SCK
        LATCbits.LATC5               = 1;    // SDO
        TRISCbits.TRISC3             = 0;    // SCK
        TRISCbits.TRISC4             = 1;    // SDI
        TRISCbits.TRISC5             = 0;    // SDO
    
        SSPSTAT_REG = 0x40;
        SSPCON1_REG = 0x20;
    #endif

    //-------------------------------------------------------------------------
    // This section is required for application-specific hardware
    // initialization.
    //-------------------------------------------------------------------------

    #if defined (__18F4620)
        // D1 and D2 are on RA0 and RA1 respectively, and CS of the TC77 is on RA2.
        // Make PORTA digital I/O.
        ADCON1 = 0x0B;//RA3 entrada analoga
    
        // Deselect the TC77 temperature sensor (RA2)
        LATA = 0x04;
    	TRISD = 0x00;
        // Make RA0, RA1, RA2 and RA4 outputs.
        TRISA = 0xE8;// Antes 0xE0;
    #endif

    // Clear the RBIF flag (INTCONbits.RBIF)
    INTCONbits.RBIF = 0;

    // Enable PORTB pull-ups (INTCON2bits.RBPU)
    INTCON2bits.RBPU = 0;

    // Make the PORTB switch connections inputs.
    #if !defined(__18F4620)
        TRISDbits.TRISD7 = 0;
        TRISBbits.TRISB3 = 1;
        TRISDbits.TRISD0 = 0;
        TRISDbits.TRISD1 = 0;
    #endif
    TRISBbits.TRISB4 = 1;
    TRISBbits.TRISB5 = 1;
}

/*******************************************************************************
User Interrupt Handler

The stack uses some interrupts for its internal processing.  Once it is done
checking for its interrupts, the stack calls this function to allow for any
additional interrupt processing.
*******************************************************************************/

void UserInterruptHandler(void)
{

    // *************************************************************************
    // Place any application-specific interrupt processing here
    // *************************************************************************

    // Is this a interrupt-on-change interrupt?
    if ( INTCONbits.RBIF == 1 )
    {
        // Record which button was pressed so the main() loop can
        // handle it
        if (BROADCAST_SWITCH == 0)
            myStatusFlags.bits.bBroadcastSwitchToggled = TRUE;

        if (LIGHT_SWITCH == 0){
            myStatusFlags.bits.bLightSwitchToggled = TRUE;
			//ConsolePutROMString((ROM char *)"en la interrupcion \r\n" );
		}
        // Disable further RBIF until we process it
        INTCONbits.RBIE = 0;

        // Clear mis-match condition and reset the interrupt flag
        LATB = PORTB;

        INTCONbits.RBIF = 0;
    }
}
