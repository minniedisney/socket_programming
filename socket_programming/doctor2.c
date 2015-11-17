/*
 ** listener.c -- a datagram sockets "server" demo
 */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/wait.h>
#include <signal.h>
#define MYPORT "42414"
#define MAXBUFLEN 100
// the port users will be connecting to
// get sockaddr, IPv4 or IPv6:
/*from beej guide*/
void *get_in_addr(struct sockaddr *sa)
{
    if (sa->sa_family == AF_INET) {
        return &(((struct sockaddr_in*)sa)->sin_addr);
    }
    return &(((struct sockaddr_in6*)sa)->sin6_addr);
}
int main(void)
{
    int sockfd;
    struct addrinfo hints, *servinfo, *p;
    int rv;
    int numbytes;
    char din[100][100] = {0};
    
    FILE *fp;
    fp = fopen("doc2.txt","r");
    int m;
    
    for(m=0;m<6;m++){
        fscanf(fp,"%s",din[m]);
        
    }
    
    
    struct hostent *lh;//get IP address of localhost
    struct in_addr **addr_list;
    
    lh = gethostbyname("localhost");
    
    // print information about this host:
    
    // printf("IP addresses: ");
    addr_list = (struct in_addr **)lh->h_addr_list;
    printf("Phase 3: Doctor2 has a static port number 42414 ");
    int z;
    for(z = 0; addr_list[z] != NULL; z++) {
        printf("and IP address %s ", inet_ntoa(*addr_list[z]));
    }
    printf("\n");
    
    
    
    
    struct sockaddr_storage their_addr;
    char buf[MAXBUFLEN];
    socklen_t addr_len;
    char s[INET6_ADDRSTRLEN];
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC; // set to AF_INET to force IPv4
    hints.ai_socktype = SOCK_DGRAM;
    hints.ai_flags = AI_PASSIVE; // use my IP
    if ((rv = getaddrinfo(NULL, MYPORT, &hints, &servinfo)) != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
        return 1;
    }
    // loop through all the results and bind to the first we can
    for(p = servinfo; p != NULL; p = p->ai_next) {
        if ((sockfd = socket(p->ai_family, p->ai_socktype,
                             p->ai_protocol)) == -1) {
            perror("listener: socket");
            continue; }
        if (bind(sockfd, p->ai_addr, p->ai_addrlen) == -1) {
            close(sockfd);
            perror("listener: bind");
            continue; }
        break; }
    if (p == NULL) {
        fprintf(stderr, "listener: failed to bind socket\n");
        return 2;
    }
    freeaddrinfo(servinfo);
 
    
    while(1) {
        
        addr_len = sizeof their_addr;
        if ((numbytes = recvfrom(sockfd, buf, MAXBUFLEN-1 , 0,
                                 (struct sockaddr *)&their_addr, &addr_len)) == -1) {
            perror("recvfrom");
            exit(1);
        }
        
        
        buf[numbytes] = '\0';
        printf("%s\n",buf);
        
        char c1[]=" ";
        char un[20];
        char ins[20];
        char *p1=strtok(buf,c1);
        memcpy(un, p1, 20);
        p1 = strtok(NULL,c1);
        memcpy(ins, p1, 20);
        
        
    
        struct sockaddr_in addrudp;
        memset(&addrudp,10,sizeof(addrudp));
        int lenudp = sizeof addrudp;
        char cip[20];
        getpeername(sockfd, (struct sockaddr *)&addrudp, (socklen_t *)&lenudp);
        inet_ntop(AF_INET, &addrudp.sin_addr, cip, sizeof cip);
        int portudp=addrudp.sin_port;
       
        
        
        
        printf("Phase 3: Doctor 2 receives the request from the patient with port number %d and name %s with the insurance plan %s.\n",portudp,un,ins);
        
        if(strcmp(ins,"insurance1")==0){
            printf("Phase 3: Doctor 2 has sent estimated price $%s to patient named %s with port number %d.\n",din[1],un,portudp);
            if ((numbytes = sendto(sockfd, din[1],100, 0,
                                   (struct sockaddr *)&their_addr, addr_len)) == -1) {
                perror("talker: sendto");
                exit(1);    }
            
            
            
        }
        
        if(strcmp(ins,"insurance2")==0){
            printf("Phase 3: Doctor 2 has sent estimated price $%s to patient named %s with port number %d.\n",din[3],un,portudp);
            if ((numbytes = sendto(sockfd, din[3],100, 0,
                                   (struct sockaddr *)&their_addr, addr_len)) == -1) {
                perror("talker: sendto");
                exit(1); }
        }
        
        if(strcmp(ins,"insurance3")==0){
            printf("Phase 3: Doctor 2 has sent estimated price $%s to patient named %s with port number %d.\n",din[5],un,portudp);
            if ((numbytes = sendto(sockfd, din[5],100, 0,
                                   (struct sockaddr *)&their_addr, addr_len)) == -1) {
                perror("talker: sendto");
                exit(1); }
            
        }
        
    }
    
    
    
    close(sockfd);
    return 0; }
