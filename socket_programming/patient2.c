#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#define PORT "21414" // the port client will be connecting to
#define MAXDATASIZE 100 // max number of bytes we can get at once
// get sockaddr, IPv4 or IPv6:
/* from beej guide*/
void *get_in_addr(struct sockaddr *sa)
{
    if (sa->sa_family == AF_INET) {
        return &(((struct sockaddr_in*)sa)->sin_addr);
    }
    return &(((struct sockaddr_in6*)sa)->sin6_addr);
}
int main(int argc, char *argv[])
{
    
    char temp[50]="available";
    char prefix[50]="authenticate ";
    int sockfd, numbytes;
    FILE *fp;
    fp = fopen("patient2.txt","r");
    char p1un[20];
    char bk[10]=" ";
    char p1pw[20];
    
    fscanf(fp,"%s %s",p1un,p1pw);
    fclose(fp);
    strcat(p1un,bk);
    strcat(prefix,p1un);
    strcat(prefix,p1pw);
    char buf[100];
    
    char ins[20];
    FILE *fp2;
    fp2 = fopen("patient2insurance.txt","r");
    fscanf(fp2,"%s",ins);
    strcat(p1un,ins);
    
    struct addrinfo hints, *servinfo, *p;
    
    int rv;
    char s[INET6_ADDRSTRLEN];
    
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    if ((rv = getaddrinfo("localhost", PORT, &hints, &servinfo)) != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
        return 1;
    }
    // loop through all the results and connect to the first we can
    for(p = servinfo; p != NULL; p = p->ai_next) {
        if ((sockfd = socket(p->ai_family, p->ai_socktype,
                             p->ai_protocol)) == -1) {
            perror("client: socket");
            continue; }
        if (connect(sockfd, p->ai_addr, p->ai_addrlen) == -1) {
            close(sockfd);
            perror("client: connect");
            continue;
            
        }
        break; }
    
    
    
    if (p == NULL) {
        fprintf(stderr, "client: failed to connect\n");
        return 2;
    }
    
    
    
    inet_ntop(p->ai_family, get_in_addr((struct sockaddr *)p->ai_addr),
              s, sizeof s);
    
    freeaddrinfo(servinfo);
    
    if (send(sockfd, prefix, 50, 0) == -1){
        perror("send");
    }
    
    //get port number
    struct sockaddr_in addr;
    memset(&addr,0,sizeof(addr));
    int len = sizeof addr;
    getsockname(sockfd, (struct sockaddr *)&addr, (socklen_t *)&len);
    
    char cip[20];
    inet_ntop(AF_INET, &addr.sin_addr, cip, sizeof cip);
    int port=addr.sin_port;
    printf("Phase 1: Patient 2 has TCP port number %d and IP address %s.\n",port,cip);
    
    
    
    
    printf("Phase 1: Authentication request from Patient 2 with username %sand password %s has been sent to the Health Center Server.\n",p1un,p1pw); //!!!
    
    char buf2[100];
    if ((numbytes = recv(sockfd, buf2, 100, 0)) == -1) {
        perror("recv");
        exit(1); }
    buf2[numbytes] = '\0';
    printf("Phase 1: Patient 2 authentication result: %s.\n",buf2);
    printf("Phase 1: End of Phase1 for Patient1.\n");
    //phase1
    
    if (send(sockfd, temp, 50, 0) == -1){
        perror("send");
    }
    
    if ((numbytes = recv(sockfd, buf, 100, 0)) == -1) {
        perror("recv");
        exit(1); }
    buf[numbytes] = '\0';
    printf("Phase 2: The following appointments are available for Patient 2:\n");
    printf("%s\n",buf);
    
    char a[100];
    printf("Phase 2: Please enter the preferred appointment index and press enter: ");
    gets(a);
    FILE *pout;
    pout=fopen("out.txt","w");
    fprintf(pout,"%s\n",a);
    
    if (send(sockfd, a, 100, 0) == -1){
        perror("send");
    }
    
    char buf1[100];
    if ((numbytes = recv(sockfd, buf1, 100, 0)) == -1) {
        perror("recv");
        exit(1); }
    buf1[numbytes] = '\0';
    printf("Phase 2: The requested appointment is available and reserved to Patient 2. The assigned doctor port number is %s\n",buf1);
    
    
    close(sockfd);
    
    
    
    
    //UDP
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_DGRAM;
    if ((rv = getaddrinfo("127.0.0.1", buf1, &hints, &servinfo)) != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
        return 1;
    }
    
    
    // loop through all the results and make a socket
    for(p = servinfo; p != NULL; p = p->ai_next) {
        if ((sockfd = socket(p->ai_family, p->ai_socktype,
                             p->ai_protocol)) == -1) {
            perror("talker: socket");
            continue; }
        break; }
    
    if (p == NULL) {
        fprintf(stderr, "talker: failed to bind socket\n");
        return 2;
    }
    freeaddrinfo(servinfo);
    
    
    
    if ((numbytes = sendto(sockfd, p1un, 20, 0,
                           p->ai_addr, p->ai_addrlen)) == -1) {
        perror("talker: sendto");}
    
    
   
    struct sockaddr_in addrudp;
    memset(&addrudp,0,sizeof(addrudp));
    int lenudp = sizeof addrudp;
    getsockname(sockfd, (struct sockaddr *)&addrudp, (socklen_t *)&lenudp);
    inet_ntop(AF_INET, &addrudp.sin_addr, cip, sizeof cip);
    int portudp=addrudp.sin_port;
    printf("Phase 3: Patient 2 has a dynamic UDP port number %d and IP address %s.\n",portudp,cip);
    
    printf("Phase 3: The cost estimation request from Patient 2 with insurance plan %s has been sent to the doctor with port number %s and IP address %s.\n",ins,buf1,cip);
    
    
    char buf3[100];
    if ((numbytes = recvfrom(sockfd, buf3, 100 , 0, NULL, NULL)) == -1) {
        perror("recvfrom");
        exit(1);
    }
    buf3[numbytes] = '\0';
    
    char name[10];
    if(strcmp(buf1,"41414")==0){
        memcpy(name, "doctor1", 10);
    }
    else{memcpy(name, "doctor2", 10);}
    printf("Phase 3: Patient 2 receives $%s estimation cost from doctor with port number %s and name %s.\n",buf3,buf1,name);
    
    close(sockfd);
    
    printf("Phase 3: End of Phase 3 for Patient 2.\n");
    
    return 0; }





