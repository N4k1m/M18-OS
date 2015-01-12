#ifndef __AGDOCPROTOCOL__
#define __AGDOCPROTOCOL__

typedef struct AGDOCProtocol_t
{
        enum AGDOCPCommand {PAUSE, RESUME, STOP, SHUTDOWN_NOW};

        // Command
        AGDOCPCommand command;

        // Query content. Union for extended possibilities
        union AGDOCPContent
        {
            // STOP params
            struct AGDOCP_STOP
            {
                int delay;
            } stop;

        } content;

} AGDOCProtocol;

#endif /* __AGDOCPROTOCOL__ */

