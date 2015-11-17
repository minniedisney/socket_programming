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
#define PORT "21414"  // the port users will be connecting to
#define MAXDATASIZE 100
#define BACKLOG 2   // how many pending connections queue will hold
void sigchld_handler(int s)
{
    while(waitpid(-1, NULL, WNOHANG) > 0);
}
// get sockaddr, IPv4 or IPv6:
void *get_in_addr(struct sockaddr *sa)
{
    if (sa->sa_family == AF_INET) {
        return &(((struct sockaddr_in*)sa)->sin_addr);
    }
    return &(((struct sockaddr_in6*)sa)->sin6_addr);
}
int main(int argc, char *argv[])
{
    
    int sockfd, new_fd;  // listen on sock_fd, new connection on new_fd
    char temp[50]="available";
    char in[100][100] = {0};
    
    FILE *fp1;
    fp1 = fopen("users.txt","r");
    char sp1un[100];
    char sp1pw[100];
    char sp2un[100];
    char sp2pw[100];
    struct hostent *lh;//get IP address of localhost
    struct in_addr **addr_list;
    
    lh = gethostbyname("localhost");
    
    // print information about this host:
    
    // printf("IP addresses: ");
    addr_list = (struct in_addr **)lh->h_addr_list;
    printf("Phase 1: The Health Center Server has port number 21414 ");
    int z;
    for(z= 0; addr_list[z] != NULL; z++) {
        printf("and ip address %s ", inet_ntoa(*addr_list[z]));
    }
    printf("\n");
    
    fscanf(fp1,"%s %s %s %s ",sp1un,sp1pw,sp2un,sp2pw);
    
    struct addrinfo hints, *servinfo, *p; //指向结果，建立之后使用的结构
    struct sockaddr_storage their_addr; // connector's（客户端） address information
    socklen_t sin_size;
    struct sigaction sa;
    int yes=1;
    char s[INET6_ADDRSTRLEN];
    //char buf[MAXDATASIZE];
    int rv,numbytes;
    memset(&hints, 0, sizeof hints); //确保struct是空的
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM; // tcp stream sockets
    hints.ai_flags = AI_PASSIVE; // use my IP，要改？改下面的null
    if ((rv = getaddrinfo(NULL, PORT, &hints, &servinfo)) != 0) {
        //纠错！有错误返回非0，正常情况下serveinfo指向struct addrinfo的链表，addrinfo包含struct sockaddr
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
        return 1;
    }
    // loop through all the results and bind to the first we can
    for(p = servinfo; p != NULL; p = p->ai_next) {
        if ((sockfd = socket(p->ai_family, p->ai_socktype, //make a socket
                             p->ai_protocol)) == -1) {
            perror("server: socket");
            continue; }
        if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes,
                       sizeof(int)) == -1) {
            perror("setsockopt");
            exit(1); }
        if (bind(sockfd, p->ai_addr, p->ai_addrlen) == -1) { //bind to a specific local IP address after having socket  为server指定端口
            close(sockfd);
            perror("server: bind");
            continue; }
        break; }
    if (p == NULL)  {
        fprintf(stderr, "server: failed to bind\n");
        return 2;
    }
    freeaddrinfo(servinfo); // all done with this structure
    if (listen(sockfd, BACKLOG) == -1) {
        perror("listen");
        exit(1);
    }
    sa.sa_handler = sigchld_handler; // reap all dead processes
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = SA_RESTART;
    if (sigaction(SIGCHLD, &sa, NULL) == -1) {
        perror("sigaction");
        exit(1); }
    
    
    while(1) {  // main accept() loop
        sin_size = sizeof their_addr;
        new_fd = accept(sockfd, (struct sockaddr *)&their_addr, &sin_size);
        if (new_fd == -1) {
            perror("accept");
            continue; }
        inet_ntop(their_addr.ss_family,
                  get_in_addr((struct sockaddr *)&their_addr),
                  s, sizeof s);
        
        if (!fork()) { // this is the child process
            close(sockfd); // child doesn't need the listener
            
            
            
            FILE *fp2;
            fp2 = fopen("availabilities.txt","r");
            //fclose(fp2);
            
            FILE *fp3;
            int flag,i,j,a;
            fp3=fopen("out.txt","r");
            fscanf(fp3,"%d",&flag);
            
            for(i=0;i<(flag-1)*5;i++){
                fscanf(fp2,"%s",in[i]);
                
            }
            //fclose(fp3);
            
            
            FILE *fp4;
            fp4 = fopen("availabilities.txt","r");
            for(j=0;j<flag;j++){
                fscanf(fp4,"%*[^\n]%*c");
            }
            
            for(a=flag*5;a<30;a++){
                fscanf(fp4,"%s",in[a]);
                
            }
            // fclose(fp4);
            
            
            
            
            
            char buf2[100];
            if ((numbytes = recv(new_fd, buf2, 100, 0)) == -1) {
                perror("recv");
                exit(1); }
            buf2[numbytes] = '\0';
            
            
            char c1[]=" ";
            char *p1=strtok(buf2,c1);
            p1 = strtok(NULL,c1);
            char pun[20];
            char ppw[20];
            memcpy(pun, p1, 20);
            p1 = strtok(NULL,c1);
            memcpy(ppw, p1, 20);
            printf("Phase 1: The Health Center Server has received request from a patient with username %s and password %s.\n",pun,ppw);
            
            if(strcmp(pun,sp1un)==0){
                
                if(strcmp(ppw,sp1pw)==0){
                    printf("Phase 1: The Health Center Server sends the response success to patient with username patient1.\n");
                    if (send(new_fd, "success", 50, 0) == -1)
                    perror("send");
                    
                }
                else{
                    printf("Phase 1: The Health Center Server sends the response failure to patient with username patient1.\n");
                    if (send(new_fd, "failure", 50, 0) == -1){
                        perror("send");
                    }
                    close(new_fd);
                    exit(0);
                }
            }
            if(strcmp(pun,sp2un)==0){
                p1 = strtok(NULL,c1);
                if(strcmp(ppw,sp2pw)==0){
                    printf("Phase 1: The Health Center Server sends the response success to patient with username patient2.\n");
                    if (send(new_fd, "success", 50, 0) == -1)
                    perror("send");
                }
                else{
                    printf("Phase 1: The Health Center Server sends the response failure to patient with username patient2.\n");
                    if (send(new_fd, "failure", 50, 0) == -1){
                        perror("send");
                    }
                    close(new_fd);
                    exit(0);
                }
            }
            
            
            
            char buf[100];
            
            if ((numbytes = recv(new_fd, buf, 100, 0)) == -1) {
                perror("recv");
                exit(1); }
            buf[numbytes] = '\0';
            
            if(strcmp(buf,temp)==0){
                
                
                
                
                struct sockaddr_in addr;
                
                memset(&addr,0,sizeof(addr));
                int len = sizeof addr;
                getpeername(new_fd, (struct sockaddr *)&addr, (socklen_t *)&len);
                
                char cip[20];
                inet_ntop(AF_INET, &addr.sin_addr, cip, sizeof cip);
                int port=addr.sin_port;
                printf("Phase 2: The Health Center Server, receives a request for available time slots from patients with port number %d and IP address %s.\n",port,cip);
                
                
                char c[100][100];
                memcpy(c, in, sizeof(in));
                char b[10]=" ";
                int o,e,n;
                for(o=0;o<30;o+=5){
                    strcat(c[o+1],b);
                    strcat(c[o],b);
                    strcat(c[o],c[o+1]);
                    strcat(c[o],c[o+2]);
                }
                char d[10]=" / ";
                for(n=0;n<31;n+=5){
                    strcat(c[n],d);
                }
                for(e=25;e>4;e-=5){
                    strcat(c[e-5],c[e]);
                }
                
                if (send(new_fd,c[0], 100, 0) == -1){
                    perror("send");}
            }
            printf("Phase 2: The Health Center Server sends available time slots to patient with username %s.\n",pun);
            
            
            char buf1[100];
            if ((numbytes = recv(new_fd, buf1, 100, 0)) == -1) {
                perror("recv");
                exit(1); }
            buf1[numbytes] = '\0';
            
            struct sockaddr_in addr;
            
            memset(&addr,0,sizeof(addr));
            int len = sizeof addr;
            getpeername(new_fd, (struct sockaddr *)&addr, (socklen_t *)&len);
            
            int port=addr.sin_port;
            
            printf("Phase 2: The Health Center Server receives a request for appointment %s from patient with port number %d and username %s.\n",buf1,port,pun);
            
            int flag1=-1;
            int x;
            for(x=0;x<30;x+=5){
                if(strcmp(buf1,in[x])==0){
                    
                    flag1=0;
                    break;
                }
            }
            if(flag1==0){
                char b[10]=" ";
                strcat(in[x],b);
                strcat(in[x+1],b);
                strcat(in[x+2],b);
                strcat(in[x+3],b);
                strcat(in[x],in[x+1]);
                strcat(in[x],in[x+2]);
                strcat(in[x],in[x+3]);
                strcat(in[x],in[x+4]);
                printf("Phase 2: The Health Center Server sends the following appointments %s from patient with port number and username %s\n",in[x],pun);
                
                int y=atoi(buf1);
                
                if (send(new_fd,in[5*y-1], 100, 0) == -1){
                    perror("send");}
            }
            
            else{
                if (send(new_fd,"Phase 2: The requested appointment is not available. Exiting...", 100, 0) == -1){
                    perror("send");}
                close(new_fd);
                exit(0);
                break;
            }
            
            close(new_fd);
            exit(0); }
        close(new_fd);  // parent doesn't need this
    }
    return 0; }



