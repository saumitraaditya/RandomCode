/*
 ============================================================================
 Name        : bst.c
 Author      : some_author not me--reference-code
 Version     : I changed delete
 Copyright   : Your copyright notice
 Description : Hello World in C, Ansi-style
 ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <malloc.h>
#define TRUE 1
#define FALSE 0

struct btreenode
{
	struct btreenode *leftchild;
	int data;
	struct btreenode *rightchild;
};

/* insert a new node in the bst*/
void insert(struct btreenode **sr,int num)
{
	if (*sr == NULL)
	{
		*sr = malloc(sizeof(struct btreenode));
		(*sr)->leftchild = NULL;
		(*sr)->rightchild = NULL;
		(*sr)->data = num;

	}
	else// find the parent node for this node
	{
		if (num < (*sr)->data)
		{
			insert(&((*sr)->leftchild),num);
		}
		else
			insert(&((*sr)->rightchild),num);
	}
	return;
}

int findrightmost(struct btreenode *sr)
{
	if (sr->rightchild==NULL)
		return sr->data;
	else{
			return findrightmost(sr->rightchild);
	}
}

void delete(struct btreenode **sr,int num)
{
	int rightmost = -9999;
	if (*sr == NULL)
	{
		printf("\nNode to be deleted not found\n");
	}
	else
	{
		if ((*sr)->data == num)
		{
			if ((*sr)->rightchild==NULL)
			{
				*sr = (*sr)->leftchild;
			}
			else if ((*sr)->leftchild==NULL)
			{
				*sr = (*sr)->rightchild;
			}
			else
			{
				rightmost = findrightmost((*sr)->leftchild);
				(*sr)->data = rightmost;
				delete(&((*sr)->leftchild),rightmost);
			}
		}
		else if (num > (*sr)->data)
		{
			delete(&((*sr)->rightchild),num);
		}
		else
		{
			delete(&((*sr)->leftchild),num);
		}
	}


}



/*dispaly the contents of the bst in in-order fashion*/
void inorder (struct btreenode *sr)
{
	if (sr != NULL)
	{
		inorder(sr->leftchild);
		printf("%d\t",sr->data);
		inorder(sr->rightchild);
	}
	else
		return;
}

int main(void)
{
	struct btreenode *bt;
	int req,i = 0,num,a[]={11,9,13,8,10,12,14,15,7};
	bt = NULL;
	while(i<=8)
	{
		insert(&bt,a[i]);
		i++;
	}
	printf("Binary Search Tree is \n");
	inorder(bt);

	delete(&bt,9);

	printf("\n Binary Search Tree after deletion is \n");
	inorder(bt);

	return EXIT_SUCCESS;
}
